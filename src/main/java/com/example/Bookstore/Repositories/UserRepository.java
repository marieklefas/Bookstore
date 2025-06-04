package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.Bookstore.DataBases.User;

/**
 * Репозиторий для работы с пользователями системы.
 * Предоставляет стандартные CRUD-операции через JpaRepository
 * и специализированные методы для работы с учетными записями пользователей.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по имени пользователя (логину).
     *
     * @param username Имя пользователя для поиска
     * @return Optional с найденным пользователем или пустой, если пользователь не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Находит пользователя по имени пользователя и статусу.
     *
     * @param username Имя пользователя для поиска
     * @param status Статус пользователя (например, "Активен", "Заблокирован")
     * @return Optional с найденным пользователем или пустой, если пользователь не найден
     */
    Optional<User> findByUsernameAndStatus(String username, String status);
}
