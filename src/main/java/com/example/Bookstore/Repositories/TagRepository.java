package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.Bookstore.DataBases.Tag;

/**
 * Репозиторий для работы с тегами книг в системе.
 * Обеспечивает базовые операции CRUD через JpaRepository
 * и специализированные методы для работы с тегами.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    /**
     * Находит тег по точному названию.
     *
     * @param name Название тега для поиска
     * @return Optional, содержащий найденный тег или пустой, если тег не найден
     */
    Optional<Tag> findByName(String name);
}
