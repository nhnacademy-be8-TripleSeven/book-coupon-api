package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryCouponCreationRequestDTO {
    private Long categoryId;
    private Long couponPolicyId;
    private String name;
}
