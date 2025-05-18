package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    int countByUser(User user);
    List<Order> findByUser(User user);
    Optional<Order> findById(String id);
    List<Order> findByOrderDateAfter(LocalDateTime startDate);
}
