package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Bookstore.DataBases.Genre;

/**
 * Репозиторий для работы с жанрами книг в базе данных.
 * Предоставляет стандартные CRUD-операции через JpaRepository.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
