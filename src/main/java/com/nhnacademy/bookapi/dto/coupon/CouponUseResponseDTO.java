package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.Coupon;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CouponUseResponseDTO {
    private Long couponId;
    private String name;
    private String couponPolicyName;
    private Long memberId;
    private LocalDate couponIssueDate;
    private String couponStatus;
    private LocalDateTime couponUseAt;

    public CouponUseResponseDTO(Coupon coupon) {
        this.couponId = coupon.getId();
        this.name = coupon.getName();
        this.couponPolicyName = coupon.getCouponPolicy().getName();
        this.memberId = coupon.getMemberId();
        this.couponIssueDate = coupon.getCouponIssueDate();
        this.couponStatus = coupon.getCouponStatus().name();
        this.couponUseAt = coupon.getCouponUseAt();
    }
}


