package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CouponResponseDTO {

    private long id;

    private CouponPolicy couponPolicy;

    private String name;

    private long memberId;

    private LocalDate couponIssueDate;

    private LocalDate couponExpiryDate;

    private String couponStatus;

    private LocalDateTime couponUseAt;

}
