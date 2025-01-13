package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponBulkCreationRequestDTO {
    private String name;           // Coupon name
    private Long couponPolicyId;   // Coupon policy ID
    private Long categoryId;       // Category ID (if applicable)
    private Long bookId;           // Book ID (if applicable)
    private long quantity;         // Number of coupons to create
}
