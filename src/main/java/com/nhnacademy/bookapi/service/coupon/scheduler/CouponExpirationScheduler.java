package com.nhnacademy.bookapi.service.coupon.scheduler;

import com.nhnacademy.bookapi.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponExpirationScheduler {
    private final CouponService couponService;

    // 매일 자정 쿠폰
    @Scheduled(cron = "0 0 0 * * *")
    public void processExpiredCoupons() {
        couponService.expireCoupons();
    }
}
