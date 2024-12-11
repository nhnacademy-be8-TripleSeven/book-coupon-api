package com.nhnacademy.bookapi.repository;

import com.nhnacademy.bookapi.entity.CategoryCoupon;
import com.nhnacademy.bookapi.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
    Optional<CategoryCoupon> findByCoupon(Coupon coupon);
}
