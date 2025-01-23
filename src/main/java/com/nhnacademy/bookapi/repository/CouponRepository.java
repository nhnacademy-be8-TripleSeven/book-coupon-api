package com.nhnacademy.bookapi.repository;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponStatus;
import com.nhnacademy.bookapi.repository.querydsl.coupon.CouponRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
    List<Coupon> findByCouponStatusAndCouponExpiryDateBefore(CouponStatus status, LocalDate now);
    List<Coupon> findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(Long memberId, LocalDate date);
    List<Coupon> findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(Long memberId, CouponStatus status, LocalDate date);

    @Query(value = "SELECT * FROM coupon WHERE member_id IS NULL AND name = :name ORDER BY id ASC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<Coupon> findAndLockFirstByName(@Param("name") String name);

    Optional<Coupon> findByName(String couponName);
}

