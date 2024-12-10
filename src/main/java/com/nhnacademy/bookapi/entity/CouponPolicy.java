package com.nhnacademy.bookapi.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    private Long couponMinAmount;

    @Setter
    private Double couponDiscountRate;

    @Setter
    private Long couponDiscountAmount;

    @Setter
    private Long couponMaxAmount;

    @Setter
    private Integer couponValidTime;
}
