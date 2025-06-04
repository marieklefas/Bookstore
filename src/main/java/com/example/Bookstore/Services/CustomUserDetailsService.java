package com.example.Bookstore.Services;

import com.example.Bookstore.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.Bookstore.DataBases.User;

/**
 * Кастомный сервис для аутентификации пользователей через Spring Security.
 * Реализует интерфейс UserDetailsService для интеграции с Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Загружает пользователя по имени пользователя для аутентификации.
     * Проверяет статус пользователя (активен/удален) перед аутентификацией.
     *
     * @param username Имя пользователя для аутентификации
     * @return UserDetails с данными пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws DisabledException если аккаунт пользователя удален
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndStatus(username, "Активен")
                .or(() -> {
                    Optional<User> deletedUser = userRepository.findByUsernameAndStatus(username, "Удален");
                    if (deletedUser.isPresent()) {
                        throw new DisabledException("К сожалению, Вы удалили свой аккаунт. Для восстановления доступа напишите на почту, указанную на странице <a href='/aboutAuthor' class='text-danger underline'>«Об авторе»</a>.");
                    }
                    return Optional.empty();
                })
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRole().stream()
                        .map(role -> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList())
        );
    }
}