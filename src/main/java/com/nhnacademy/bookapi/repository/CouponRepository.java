package com.nhnacademy.bookapi.repository;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByCouponStatusAndCouponExpiryDateBefore(CouponStatus status, LocalDate now);
    List<Coupon> findByMemberId(Long memberId);
    List<Coupon> findByCouponPolicyId(Long id);
    List<Coupon> findByMemberIdAndCouponStatus(Long memberId, CouponStatus couponStatus);
}
