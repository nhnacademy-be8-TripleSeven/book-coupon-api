package com.nhnacademy.bookapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String name;

    @Setter
    private Long couponMinAmount;

    @Setter
    private Long couponMaxAmount;

    @Setter
    @Column(precision = 4, scale = 2)
    private BigDecimal couponDiscountRate;

    @Setter
    private Long couponDiscountAmount;

    @Setter
    @Column(nullable = false)
    private Integer couponValidTime;
}
