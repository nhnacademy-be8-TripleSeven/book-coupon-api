package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.Getter;

@Getter
public class CategoryCouponResponseDTO {
    private Long id;
    private String name;
    private CouponPolicy couponPolicy;
    private String categoryName;

    public CategoryCouponResponseDTO(Long id, String name, CouponPolicy couponPolicy, String categoryName) {
        this.id = id;
        this.name = name;
        this.couponPolicy = couponPolicy;
        this.categoryName = categoryName;
    }
}
