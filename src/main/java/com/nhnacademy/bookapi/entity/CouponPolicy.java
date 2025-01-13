package com.nhnacademy.bookapi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private Long couponMinAmount;

    private Long couponMaxAmount;

    @Column(precision = 4, scale = 2)
    private BigDecimal couponDiscountRate;

    private Long couponDiscountAmount;

    @Column(nullable = false)
    private Integer couponValidTime;

    public CouponPolicy(String name, Long couponMinAmount, Long couponMaxAmount,
                        BigDecimal couponDiscountRate, Long couponDiscountAmount, Integer couponValidTime) {
        this.name = name;
        this.couponMinAmount = couponMinAmount;
        this.couponMaxAmount = couponMaxAmount;
        this.couponDiscountRate = couponDiscountRate;
        this.couponDiscountAmount = couponDiscountAmount;
        this.couponValidTime = couponValidTime;
    }

    public void setCouponPolicyUpdateData(String name, Long couponMinAmount, Long couponMaxAmount,
                                          BigDecimal couponDiscountRate, Long couponDiscountAmount, Integer couponValidTime) {
        this.name = name;
        this.couponMinAmount = couponMinAmount;
        this.couponMaxAmount = couponMaxAmount;
        this.couponDiscountRate = couponDiscountRate;
        this.couponDiscountAmount = couponDiscountAmount;
        this.couponValidTime = couponValidTime;
    }

    // Test 전용 메서드
    public void setTestId(Long id) {
        this.id = id;
    }

}
