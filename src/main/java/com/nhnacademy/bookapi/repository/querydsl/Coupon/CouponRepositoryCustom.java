package com.nhnacademy.bookapi.repository.querydsl.Coupon;

import com.nhnacademy.bookapi.entity.Coupon;

import java.util.List;

public interface CouponRepositoryCustom {
    List<Coupon> findAvailableCoupons(Long memberId, Long amount, List<Long> bookIds);
}