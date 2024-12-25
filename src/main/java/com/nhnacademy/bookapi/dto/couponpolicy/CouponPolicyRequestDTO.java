package com.nhnacademy.bookapi.dto.couponpolicy;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CouponPolicyRequestDTO {

    private String name;

    private Long couponMinAmount;

    private Long couponMaxAmount;

    private BigDecimal couponDiscountRate;

    private Long couponDiscountAmount;

    private Integer couponValidTime;

}
