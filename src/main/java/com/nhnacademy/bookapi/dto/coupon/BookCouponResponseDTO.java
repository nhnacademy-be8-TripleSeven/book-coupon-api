package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookCouponResponseDTO {
    private Long id;
    private String name;
    private CouponPolicy couponPolicy;
    private String bookTitle;
}
