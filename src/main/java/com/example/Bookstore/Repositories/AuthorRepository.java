package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Bookstore.DataBases.Author;

/**
 * Репозиторий для работы с авторами книг в базе данных.
 * Предоставляет стандартные CRUD-операции через JpaRepository.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
