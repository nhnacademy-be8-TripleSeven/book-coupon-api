package com.nhnacademy.bookapi.dto.coupon;
import lombok.Getter;

@Getter
public class CouponAssignRequestDTO {
    private Long couponId;
    private Long memberId;

    public CouponAssignRequestDTO(Long couponId, Long memberId) {
        this.memberId = memberId;
        this.couponId = couponId;
    }
}
