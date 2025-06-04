package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с промокодами в системе.
 * Предоставляет стандартные CRUD-операции через JpaRepository
 * и специализированные методы для работы с промокодами.
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    /**
     * Находит промокод по его коду (без учета регистра).
     *
     * @param promoCode Код промокода для поиска
     * @return Optional с найденным промокодом или пустой, если промокод не найден
     */
    Optional<PromoCode> findByCodeIgnoreCase(String promoCode);

    /**
     * Получает все промокоды, отсортированные по дате окончания действия (от новых к старым).
     *
     * @return Список всех промокодов, отсортированных по дате окончания в порядке убывания
     */
    List<PromoCode> findAllByOrderByEndDateDesc();
}
