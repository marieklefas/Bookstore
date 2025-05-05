package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.Order;
import com.example.Bookstore.DataBases.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
}
