package com.example.Bookstore.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Bookstore.DataBases.*;
import com.example.Bookstore.Repositories.*;
import java.util.List;

/**
 * Сервис для работы с книгами.
 * Предоставляет бизнес-логику для операций с книгами, авторами, жанрами и другими связанными сущностями.
 */
@Service
public class BookService {
    @Autowired private BookRepository bookRepository;

    /**
     * Получает список всех книг в системе.
     *
     * @return Список всех книг
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Находит книги по имени автора.
     *
     * @param authorName Имя автора для поиска
     * @return Список книг указанного автора
     */
    public List<Book> findBooksByAuthor(String authorName) {
        return bookRepository.findByAuthorsName(authorName);
    }

    /**
     * Находит книги по названию жанра.
     *
     * @param genreName Название жанра для поиска
     * @return Список книг указанного жанра
     */
    public List<Book> findBooksByGenre(String genreName) {
        return bookRepository.findByGenresName(genreName);
    }

    /**
     * Уменьшает количество доступных экземпляров книги на 1.
     * Используется при оформлении заказов.
     *
     * @param bookId ID книги
     * @throws RuntimeException если книга не найдена
     * @throws IllegalStateException если нет доступных экземпляров
     */
    public void decreaseAvailableAmount(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Книга не найдена"));
        if (book.getAvailableAmount() > 0) {
            book.setAvailableAmount(book.getAvailableAmount() - 1);
            bookRepository.save(book);
        } else {
            throw new IllegalStateException("Нет экземпляров в наличии");
        }
    }
}
