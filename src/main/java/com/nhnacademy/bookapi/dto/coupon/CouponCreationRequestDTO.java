package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CouponCreationRequestDTO {
    private String name;
    private Long CouponPolicyId;
}
