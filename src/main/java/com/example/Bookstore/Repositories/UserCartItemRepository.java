package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с элементами корзины пользователей.
 * Предоставляет стандартные CRUD-операции через JpaRepository
 * и специализированные методы для управления корзиной покупок.
 */
public interface UserCartItemRepository extends JpaRepository<UserCartItem, Long> {
    /**
     * Находит конкретный элемент корзины по пользователю и книге.
     * Результаты сортируются по названию книги в возрастающем порядке.
     *
     * @param user Пользователь, чей элемент корзины ищется
     * @param book Книга в корзине
     * @return Optional с найденным элементом корзины или пустой, если не найден
     */
    Optional<UserCartItem> findByUserAndBookOrderByBookAsc(User user, Book book);

    /**
     * Находит все элементы корзины указанного пользователя.
     * Элементы сортируются по названию книги в возрастающем порядке.
     *
     * @param user Пользователь, чью корзину нужно получить
     * @return Список элементов корзины пользователя
     */
    List<UserCartItem> findByUserOrderByBookAsc(User user);

    /**
     * Удаляет конкретный элемент из корзины пользователя.
     *
     * @param user Пользователь, чей элемент удаляется
     * @param book Книга, которую нужно удалить из корзины
     */
    void deleteByUserAndBook(User user, Book book);

    /**
     * Очищает всю корзину указанного пользователя.
     *
     * @param user Пользователь, чью корзину нужно очистить
     */
    void deleteAllByUser(User user);
}
