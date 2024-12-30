package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.Coupon;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CouponDetailsDTO {
    private Long id;
    private String name;
    private Long memberId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private LocalDateTime usedAt;
    private BigDecimal discountRate; // 할인률
    private Long discountAmount;
    private String bookTitle;
    private String categoryName;

    public CouponDetailsDTO(Coupon coupon,
                            String bookTitle,
                            String categoryName) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.memberId = coupon.getMemberId();
        this.issueDate = coupon.getCouponIssueDate();
        this.expiryDate = coupon.getCouponExpiryDate();
        this.status = coupon.getCouponStatus().name();
        this.usedAt = coupon.getCouponUseAt();
        this.discountRate = coupon.getCouponPolicy().getCouponDiscountRate();
        this.discountAmount = coupon.getCouponPolicy().getCouponDiscountAmount();
        this.bookTitle = bookTitle;
        this.categoryName = categoryName;

    }
}
