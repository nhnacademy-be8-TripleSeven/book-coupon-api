package com.nhnacademy.bookapi.dto.couponpolicy;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.*;

import java.math.BigDecimal;

@ApiResponse
@NoArgsConstructor
@Getter
@Builder
public class CouponPolicyResponseDTO {

    private Long id;

    private String name;

    private Long couponMinAmount;

    private Long couponMaxAmount;

    private BigDecimal couponDiscountRate;

    private Long couponDiscountAmount;

    private Integer couponValidTime;

    public CouponPolicyResponseDTO(Long id, String name, Long couponMinAmount, Long couponMaxAmount,
                                   BigDecimal couponDiscountRate, Long couponDiscountAmount, Integer couponValidTime) {
        this.id = id;
        this.name = name;
        this.couponMinAmount = couponMinAmount;
        this.couponMaxAmount = couponMaxAmount;
        this.couponDiscountRate = couponDiscountRate;
        this.couponDiscountAmount = couponDiscountAmount;
        this.couponValidTime = couponValidTime;
    }
}
