package com.example.Bookstore.DataBases;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

@Entity
@Table(name = "PromoCode")
public class PromoCode {
    public PromoCode() {}

    public PromoCode(String code, String discountType, int discountValue, LocalDate startDate, LocalDate endDate) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @Column(name= "code", nullable = false, unique = true)
    private String code;

    @Column(name= "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name= "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name= "type", nullable = false)
    private String discountType;

    @Column(name= "value", nullable = false)
    private int discountValue;

    @OneToMany(mappedBy = "promoCode")
    private List<Order> orders = new ArrayList<>();


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public int getDiscountValue() { return discountValue; }
    public void setDiscountValue(int discountValue) { this.discountValue = discountValue; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}
