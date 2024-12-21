package com.nhnacademy.bookapi.dto.coupon;
import lombok.Getter;

@Getter
public class CouponCreationRequestDTO {
    private String name;
    private Long CouponPolicyId;

    public CouponCreationRequestDTO(String name, Long couponPolicyId) {
        this.name = name;
        CouponPolicyId = couponPolicyId;
    }
}
