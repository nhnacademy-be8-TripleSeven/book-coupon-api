package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CouponAssignRequestDTO {
    private Long couponId;
    private Long memberId;
}
