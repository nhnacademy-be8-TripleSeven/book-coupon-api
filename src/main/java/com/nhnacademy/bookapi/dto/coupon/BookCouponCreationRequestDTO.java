package com.nhnacademy.bookapi.dto.coupon;
import lombok.Getter;

@Getter
public class BookCouponCreationRequestDTO {
    private Long bookId;
    private Long couponPolicyId;
    private String name;

    public BookCouponCreationRequestDTO(Long bookId, Long couponPolicyId, String name) {
        this.bookId = bookId;
        this.couponPolicyId = couponPolicyId;
        this.name = name;
    }
}
