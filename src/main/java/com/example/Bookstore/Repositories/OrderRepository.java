package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с заказами в системе.
 * Предоставляет стандартные CRUD-операции через JpaRepository
 * и специализированные методы для поиска и фильтрации заказов.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Находит все заказы с указанным статусом.
     *
     * @param status Статус заказа для поиска (например, "Активен", "Завершен")
     * @return Список заказов с указанным статусом
     */
    List<Order> findByStatus(String status);

    /**
     * Подсчитывает количество заказов для указанного пользователя.
     *
     * @param user Пользователь, для которого подсчитываются заказы
     * @return Количество заказов пользователя
     */
    int countByUser(User user);

    /**
     * Находит все заказы указанного пользователя.
     *
     * @param user Пользователь, чьи заказы нужно найти
     * @return Список заказов пользователя
     */
    List<Order> findByUser(User user);

    /**
     * Находит заказ по его идентификатору.
     *
     * @param id Идентификатор заказа
     * @return Optional с найденным заказом или пустой, если заказ не найден
     */
    Optional<Order> findById(String id);

    /**
     * Находит заказы, сделанные после указанной даты.
     *
     * @param startDate Дата, после которой нужно искать заказы
     * @return Список заказов, созданных после указанной даты
     */
    List<Order> findByOrderDateAfter(LocalDateTime startDate);

    /**
     * Находит все заказы, отсортированные по дате создания (от новых к старым).
     *
     * @return Список всех заказов, отсортированных по дате в порядке убывания
     */
    List<Order> findAllByOrderByOrderDateDesc();
}
