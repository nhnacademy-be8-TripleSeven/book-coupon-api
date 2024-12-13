package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class CouponAssignResponseDTO {
    private Long couponId;
    private String name;
    private String couponPolicyName;
    private Long memberId;
    private LocalDate couponIssueDate;
    private LocalDate couponExpiryDate;
    private String couponStatus;

    public CouponAssignResponseDTO(Coupon coupon) {
        this.couponId = coupon.getId();
        this.name = coupon.getName();
        this.couponPolicyName = coupon.getCouponPolicy().getName(); // Lazy Loading 발생 방지
        this.memberId = coupon.getMemberId();
        this.couponIssueDate = coupon.getCouponIssueDate();
        this.couponExpiryDate = coupon.getCouponExpiryDate();
        this.couponStatus = coupon.getCouponStatus().name();
    }
}
