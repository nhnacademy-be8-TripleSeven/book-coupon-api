package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class CouponAssignResponseDTO {
    private Long couponId;
    private String name;
    private CouponPolicy couponPolicy;
    private Long memberId;
    private LocalDate couponIssueDate;
    private String couponStatus;
}
