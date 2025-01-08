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

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    private CouponPolicy couponPolicy;

    private Long memberId;

    private LocalDate couponIssueDate;

    private LocalDate couponExpiryDate;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    private LocalDateTime couponUseAt;

    public Coupon(String name, CouponPolicy policy) {
        this.name = name;
        this.couponPolicy = policy;
    }

    public void setTestId(Long id) {
        this.id = id;
    }

    public void updateCouponStatus(CouponStatus couponStatus) {
        this.couponStatus = couponStatus;
    }

    public void updateCouponUseAt(LocalDateTime time) {
        this.couponUseAt = time;
    }

    public void setCouponAssignData(Long memberId, LocalDate couponIssueDate,
                                    LocalDate couponExpiryDate, CouponStatus couponStatus) {

        this.memberId = memberId;
        this.couponIssueDate = couponIssueDate;
        this.couponExpiryDate = couponExpiryDate;
        this.couponStatus = couponStatus;
    }


}
