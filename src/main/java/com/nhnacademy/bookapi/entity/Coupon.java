package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    private CouponPolicy couponPolicy;

    @Setter
    private Long memberId;

    @Setter
    private LocalDate couponIssueDate;

    @Setter
    private LocalDate couponExpiryDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @Setter
    private LocalDateTime couponUseAt;
}
