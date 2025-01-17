package com.nhnacademy.bookapi.dto.coupon;
import lombok.Getter;

@Getter
public class CouponCreationRequestDTO {
    private String name;
    private Long couponPolicyId;

    public CouponCreationRequestDTO(String name, Long policyId){
        this.name = name;
        this.couponPolicyId = policyId;
    }
}
