package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.Getter;

@Getter
public class BaseCouponResponseDTO {
    private Long id;
    private String name;
    private CouponPolicy couponPolicy;

    public BaseCouponResponseDTO(Long id, String name, CouponPolicy couponPolicy) {
        this.id = id;
        this.name = name;
        this.couponPolicy = couponPolicy;
    }
}

