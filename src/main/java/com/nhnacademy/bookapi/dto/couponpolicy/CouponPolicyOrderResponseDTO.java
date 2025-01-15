package com.nhnacademy.bookapi.dto.couponpolicy;

import com.nhnacademy.bookapi.entity.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
@Builder
public class CouponPolicyOrderResponseDTO {
    Long couponMinAmount;

    Long couponMaxAmount;

    BigDecimal couponDiscountRate;

    Long couponDiscountAmount;

    CouponStatus couponStatus;

}
