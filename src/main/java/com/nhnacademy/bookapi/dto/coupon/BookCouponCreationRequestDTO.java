package com.nhnacademy.bookapi.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookCouponCreationRequestDTO {
    private Long bookId;
    private Long couponPolicyId;
    private String name;
}
