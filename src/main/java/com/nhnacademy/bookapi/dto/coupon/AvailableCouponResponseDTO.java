package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AvailableCouponResponseDTO {
    private Long couponId;
    private String couponName;
    private BigDecimal discountRate;
    private Long discountAmount;
    private Long maxDiscountAmount;
    private LocalDate expiryDate;
}
