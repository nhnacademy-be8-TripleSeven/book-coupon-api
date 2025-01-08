package com.nhnacademy.bookapi.dto.couponpolicy;

import com.nhnacademy.bookapi.entity.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CouponPolicyOrderResponseDTO {
    Long couponMinAmount;

    Long couponMaxAmount;

    BigDecimal couponDiscountRate;

    Long couponDiscountAmount;

    CouponStatus couponStatus;

}
