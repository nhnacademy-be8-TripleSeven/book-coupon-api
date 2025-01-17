package com.nhnacademy.bookapi.repository.querydsl.coupon;

import com.nhnacademy.bookapi.entity.Coupon;

import java.util.List;

public interface CouponRepositoryCustom {
    List<Coupon> findAvailableCoupons(Long memberId, Long amount, Long bookId);
}
