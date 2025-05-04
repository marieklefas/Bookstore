package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCartItemRepository extends JpaRepository<UserCartItem, Long> {
    Optional<UserCartItem> findByUserAndBook(User user, Book book);
    List<UserCartItem> findByUser(User user);
    void deleteByUserAndBook(User user, Book book);
}
