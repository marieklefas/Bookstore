package com.example.Bookstore.Repositories;

import com.example.Bookstore.DataBases.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeIgnoreCase(String promoCode);
    List<PromoCode> findAllByOrderByEndDateDesc();
//    List<PromoCode> findAllByOrderByExpirationDateDesc();
}
