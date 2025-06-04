package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.Bookstore.DataBases.Language;

/**
 * Репозиторий для работы с языками публикаций книг.
 * Обеспечивает базовые операции CRUD через JpaRepository
 * и специализированные методы для поиска языков.
 */
public interface LanguageRepository extends JpaRepository<Language, Long>{
    /**
     * Находит язык по его названию.
     * Поиск выполняется с точным соответствием названия.
     *
     * @param name Название языка для поиска (например, "Русский", "Английский")
     * @return Optional с найденным языком, либо пустой Optional если язык не найден
     */
    Optional<Language> findByName(String name);
}
