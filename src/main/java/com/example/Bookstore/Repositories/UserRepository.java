package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.Bookstore.DataBases.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}
