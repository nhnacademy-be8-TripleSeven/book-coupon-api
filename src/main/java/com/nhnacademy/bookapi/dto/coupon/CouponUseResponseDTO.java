package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CouponUseResponseDTO {
    private Long couponId;
    private String name;
    private CouponPolicy couponPolicy;
    private Long memberId;
    private LocalDate couponIssueDate;
    private String couponStatus;
    private LocalDateTime couponUseAt;
}
