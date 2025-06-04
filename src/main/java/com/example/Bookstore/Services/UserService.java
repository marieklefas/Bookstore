package com.example.Bookstore.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Bookstore.DataBases.User;
import com.example.Bookstore.Repositories.UserRepository;

import java.util.Collections;

/**
 * Сервис для работы с пользователями системы.
 * Обеспечивает регистрацию, управление учетными записями и безопасность паролей.
 */
@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    /**
     * Конструктор сервиса с внедрением зависимостей.
     *
     * @param userRepository Репозиторий для работы с пользователями
     * @param passwordEncoder Кодировщик паролей
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Устанавливает статус "Активен" и роль "ROLE_USER" по умолчанию.
     * Хэширует пароль перед сохранением.
     *
     * @param user Объект пользователя для регистрации
     * @return true если регистрация успешна, false если пользователь с таким логином уже существует
     */
    public boolean registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }

        user.setStatus("Активен");
        user.setRole(Collections.singleton("ROLE_USER"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return true;
    }

    /**
     * Проверяет соответствие введенного пароля хэшу пароля пользователя.
     *
     * @param user Пользователь для проверки
     * @param rawPassword Введенный пароль в открытом виде
     * @return true если пароль верный, false если неверный
     */
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * Хэширует пароль для безопасного хранения.
     *
     * @param rawPassword Пароль в открытом виде
     * @return Захэшированный пароль
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
