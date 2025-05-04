package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
}
