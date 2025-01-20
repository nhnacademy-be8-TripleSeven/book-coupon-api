package com.nhnacademy.bookapi.repository.querydsl.coupon;

import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.repository.CouponPolicyRepository;
import com.nhnacademy.bookapi.repository.CouponRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Import(CouponRepositoryImpl.class)
class CouponRepositoryImplTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;


    private CouponPolicy couponPolicy;
    private CouponPolicy couponPolicy2;

    @BeforeAll
    static void setUpGlobalData(@Autowired CouponPolicyRepository couponPolicyRepository) {

        // 쿠폰 정책 생성 (이미 있으면 재사용)
        couponPolicyRepository.findByName("Test Policy for querydsl test1")
                .orElseGet(() -> couponPolicyRepository.save(
                        CouponPolicy.builder()
                                .name("Test Policy for querydsl test1")
                                .couponMinAmount(100000L)
                                .couponMaxAmount(500000L)
                                .couponDiscountRate(new BigDecimal("0.10"))
                                .couponValidTime(30)
                                .build()
                ));

        couponPolicyRepository.findByName("Test Policy for querydsl test2")
                .orElseGet(() -> couponPolicyRepository.save(
                        CouponPolicy.builder()
                                .name("Test Policy for querydsl test2")
                                .couponMinAmount(200000L)
                                .couponMaxAmount(500000L)
                                .couponDiscountRate(new BigDecimal("0.10"))
                                .couponValidTime(30)
                                .build()
                ));

    }

    @BeforeEach
    void setUp() {
        couponPolicy = couponPolicyRepository.findByName("Test Policy for querydsl test1").orElseThrow();
        couponPolicy2 = couponPolicyRepository.findByName("Test Policy for querydsl test2").orElseThrow();
    }

    @Test
    void testFindAvailableCouponsWithBookCoupon() {


        // BookCoupon 생성
        couponRepository.findByName("Coupon for Test1")
                .orElseGet(() -> couponRepository.save(
                        Coupon.builder()
                                .name("Coupon for Test1")
                                .memberId(1L)
                                .couponPolicy(couponPolicy)
                                .couponIssueDate(LocalDate.now())
                                .couponExpiryDate(LocalDate.now().plusDays(30))
                                .couponStatus(CouponStatus.NOTUSED)
                                .build()
                ));

        couponRepository.findByName("Coupon for Test2")
                .orElseGet(() -> couponRepository.save(
                        Coupon.builder()
                                .name("Coupon for Test2")
                                .memberId(1L)
                                .couponPolicy(couponPolicy2)
                                .couponIssueDate(LocalDate.now())
                                .couponExpiryDate(LocalDate.now().plusDays(30))
                                .couponStatus(CouponStatus.NOTUSED)
                                .build()
                ));

        // 테스트 실행
        List<Coupon> availableCoupons = couponRepository.findAvailableCoupons(1L, 150000L, 1L);

        // 검증: "Coupon for Test"라는 이름을 포함하는 항목 개수 확인
        long matchingCouponsCount = availableCoupons.stream()
                .filter(coupon -> coupon.getName().contains("Coupon for Test"))
                .count();

        assertThat(matchingCouponsCount).isEqualTo(1);
    }

}

