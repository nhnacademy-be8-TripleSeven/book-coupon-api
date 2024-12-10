package com.nhnacademy.bookapi.repository;
import com.nhnacademy.bookapi.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
