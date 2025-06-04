package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Bookstore.DataBases.Book;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторий для работы с книгами в базе данных.
 * Предоставляет стандартные CRUD-операции через JpaRepository,
 * а также дополнительные методы для поиска и фильтрации книг.
 */
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    /**
     * Находит книги по имени автора.
     *
     * @param name Имя автора
     * @return Список книг указанного автора
     */
    List<Book> findByAuthorsName(String name);

    /**
     * Находит книги по названию жанра.
     *
     * @param name Название жанра
     * @return Список книг указанного жанра
     */
    List<Book> findByGenresName(String name);

    /**
     * Находит все уникальные типы обложек книг.
     *
     * @return Список уникальных типов обложек
     */
    @Query("SELECT DISTINCT b.coverType FROM Book b WHERE b.coverType IS NOT NULL")
    List<String> findDistinctCoverTypes();

    /**
     * Находит все уникальные возрастные ограничения книг.
     *
     * @return Список уникальных возрастных ограничений
     */
    @Query("SELECT DISTINCT b.ageLimit FROM Book b WHERE b.ageLimit IS NOT NULL")
    List<String> findDistinctAgeLimits();

    /**
     * Находит минимальный год публикации среди всех книг.
     *
     * @return Минимальный год публикации
     */
    @Query("SELECT MIN(b.publishingYear) FROM Book b")
    Integer findMinPublishingYear();

    /**
     * Находит максимальный год публикации среди всех книг.
     *
     * @return Максимальный год публикации
     */
    @Query("SELECT MAX(b.publishingYear) FROM Book b")
    Integer findMaxPublishingYear();

    /**
     * Находит минимальную цену среди всех книг.
     *
     * @return Минимальная цена
     */
    @Query("SELECT MIN(b.price) FROM Book b")
    Double findMinPrice();

    /**
     * Находит максимальную цену среди всех книг.
     *
     * @return Максимальная цена
     */
    @Query("SELECT MAX(b.price) FROM Book b")
    Double findMaxPrice();

    /**
     * Ищет книги по поисковому запросу (в названии или имени автора).
     *
     * @param query Поисковый запрос
     * @return Список книг, соответствующих запросу
     */
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors a WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> findBySearchQuery(@Param("query") String query);

    /**
     * Находит похожие книги по жанрам и тегам.
     * Сначала возвращает книги с совпадающими жанрами, затем с совпадающими тегами,
     * сортируя результаты по названию.
     *
     * @param bookId ID книги, для которой ищем похожие (исключается из результатов)
     * @param genreIds Список ID жанров для поиска
     * @param tagIds Список ID тегов для поиска
     * @return Список похожих книг
     */
    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.genres g " +
            "LEFT JOIN b.tags t " +
            "WHERE b.id <> :bookId AND " +
            "(g.id IN :genreIds OR t.id IN :tagIds) " +
            "ORDER BY " +
            "CASE WHEN g.id IN :genreIds THEN 0 ELSE 1 END, " + // Сначала книги с совпадающими жанрами
            "b.title ASC") // Затем сортируем по названию
    List<Book> findSimilarBooks(
            @Param("bookId") Long bookId,
            @Param("genreIds") List<Long> genreIds,
            @Param("tagIds") List<Long> tagIds);
}
