package com.example.Bookstore.Repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Bookstore.DataBases.Book;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findByAuthorsName(String name);
    List<Book> findByGenresName(String name);

    @Query("SELECT DISTINCT b.coverType FROM Book b WHERE b.coverType IS NOT NULL")
    List<String> findDistinctCoverTypes();

    @Query("SELECT DISTINCT b.ageLimit FROM Book b WHERE b.ageLimit IS NOT NULL")
    List<String> findDistinctAgeLimits();

    @Query("SELECT MIN(b.publishingYear) FROM Book b")
    Integer findMinPublishingYear();

    @Query("SELECT MAX(b.publishingYear) FROM Book b")
    Integer findMaxPublishingYear();

    @Query("SELECT MIN(b.price) FROM Book b")
    Double findMinPrice();

    @Query("SELECT MAX(b.price) FROM Book b")
    Double findMaxPrice();

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors a WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> findBySearchQuery(@Param("query") String query);

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
