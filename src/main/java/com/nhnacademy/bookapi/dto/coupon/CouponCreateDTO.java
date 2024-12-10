package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CouponCreateDTO {

    private String name;

    private CouponPolicy couponPolicy;

}
