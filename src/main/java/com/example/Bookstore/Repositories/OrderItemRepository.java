package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с позициями заказов (OrderItems) в системе.
 * Предоставляет стандартные CRUD-операции через JpaRepository.
 *
 * <p>Каждая позиция заказа (OrderItems) представляет собой связь между заказом (Order)
 * и книгой (Book) с указанием количества и цены на момент заказа.</p>
 */
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
}
