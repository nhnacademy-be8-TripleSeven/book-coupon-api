package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.Coupon;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CouponDetailsDTO {
    private Long id;
    private String policyName;
    private String name;
    private Long memberId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private LocalDateTime usedAt;
    private String bookTitle;
    private String categoryName;

    public CouponDetailsDTO(Coupon coupon,
                            String bookTitle,
                            String categoryName) {
        this.id = coupon.getId();
        this.policyName = coupon.getCouponPolicy().getName();
        this.name = coupon.getName();
        this.memberId = coupon.getMemberId();
        this.issueDate = coupon.getCouponIssueDate();
        this.expiryDate = coupon.getCouponExpiryDate();
        this.status = coupon.getCouponStatus().name();
        this.usedAt = coupon.getCouponUseAt();
        this.bookTitle = bookTitle;
        this.categoryName = categoryName;
    }
}
