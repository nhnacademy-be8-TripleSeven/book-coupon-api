package com.nhnacademy.bookapi.dto.couponpolicy;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CouponPolicyRequestDTO {

    private String name;

    private long couponMinAmount;

    private long couponMaxAmount;

    private BigDecimal couponDiscountRate;

    private long couponDiscountAmount;

    private Integer couponValidTime;

}
