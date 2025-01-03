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

    List<Coupon> findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(Long memberId, LocalDate date);
    List<Coupon> findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(Long memberId, CouponStatus status, LocalDate date);


    //osiv false
//    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.couponPolicy WHERE c.couponStatus = :status AND c.couponExpiryDate < :now")
//    List<Coupon> findByCouponStatusAndCouponExpiryDateBeforeWithPolicy(CouponStatus status, LocalDate now);
//    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.couponPolicy WHERE c.memberId = :memberId")
//    List<Coupon> findAllByMemberIdWithPolicy(Long memberId);
//    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.couponPolicy WHERE c.memberId = :memberId AND c.couponStatus = :status")
//    List<Coupon> findByMemberIdAndStatusWithPolicy(Long memberId, CouponStatus status);
//
//    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.couponPolicy WHERE c.memberId = :memberId AND c.couponIssueDate >= :date ORDER BY c.couponIssueDate DESC")
//    List<Coupon> findByMemberIdAndCouponIssueDateAfterWithPolicy(Long memberId, LocalDate date);
//    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.couponPolicy WHERE c.memberId = :memberId AND c.couponStatus = :status AND c.couponIssueDate >= :date ORDER BY c.couponUseAt DESC")
//    List<Coupon> findByMemberIdAndCouponStatusAndCouponIssueDateAfterWithPolicy(Long memberId, CouponStatus status, LocalDate date);

}

