package com.example.Bookstore.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.Bookstore.DataBases.Language;

public interface LanguageRepository extends JpaRepository<Language, Long>{
    Optional<Language> findByName(String name);
}
