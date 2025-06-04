package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.Bookstore.DataBases.Publisher;

/**
 * Репозиторий для работы с издательствами книг в системе.
 * Предоставляет стандартные CRUD-операции через JpaRepository
 * и специализированные методы для работы с издательствами.
 */
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    /**
     * Находит издательство по названию (с точным соответствием).
     *
     * @param name Название издательства для поиска
     * @return Optional, содержащий найденное издательство или пустой, если издательство не найдено
     */
    Optional<Publisher> findByName(String name);
}
