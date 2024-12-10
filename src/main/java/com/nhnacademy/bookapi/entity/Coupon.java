package com.nhnacademy.bookapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(name = "coupon_policy_id")
    private CouponPolicy couponPolicy;

    @Setter
    private Long memberId;

    @Setter
    private LocalDate couponIssueDate;

    @Setter
    private LocalDate couponExpiryDate;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @Setter
    private LocalDate couponUseAt;

    public enum CouponStatus {
        NOTUSED, USED, EXPIRED
    }
}
