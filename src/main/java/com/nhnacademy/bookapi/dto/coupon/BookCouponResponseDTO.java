package com.nhnacademy.bookapi.dto.coupon;

import com.nhnacademy.bookapi.entity.CouponPolicy;
import lombok.Getter;

@Getter
public class BookCouponResponseDTO {
    private Long id;
    private String name;
    private CouponPolicy couponPolicy;
    private String bookTitle;

    public BookCouponResponseDTO(Long id, String name, CouponPolicy couponPolicy, String bookTitle) {
        this.id = id;
        this.name = name;
        this.couponPolicy = couponPolicy;
        this.bookTitle = bookTitle;
    }
}
