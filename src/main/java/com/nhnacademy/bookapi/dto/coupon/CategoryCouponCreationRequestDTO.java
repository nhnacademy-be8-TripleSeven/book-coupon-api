package com.nhnacademy.bookapi.dto.coupon;

import lombok.Getter;

@Getter
public class CategoryCouponCreationRequestDTO {
    private Long categoryId;
    private Long couponPolicyId;
    private String name;

    public CategoryCouponCreationRequestDTO(Long categoryId, Long couponPolicyId, String name) {
        this.categoryId = categoryId;
        this.couponPolicyId = couponPolicyId;
        this.name = name;
    }
}
