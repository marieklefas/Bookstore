package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Bookstore.DataBases.Genre;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
}
