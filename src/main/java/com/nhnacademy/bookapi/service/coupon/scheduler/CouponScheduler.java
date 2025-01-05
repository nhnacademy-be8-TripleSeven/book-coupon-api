package com.nhnacademy.bookapi.service.coupon.scheduler;

import com.nhnacademy.bookapi.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {
    private final CouponService couponService;

    // 매일 자정 쿠폰
    @Scheduled(cron = "0 0 0 * * *")
    public void processExpiredCoupons() {
        log.info("Coupon Expired.");
        couponService.expireCoupons();
    }


    /**
     * 매월 1일 0시에 실행되어 해당 월 생일자에게 쿠폰 지급
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void assignMonthlyBirthdayCoupons() {
        log.info("Starting monthly birthday coupon assignment...");
        try {
            couponService.assignMonthlyBirthdayCoupons();
            log.info("Monthly birthday coupon assignment completed successfully.");
        } catch (Exception e) {
            log.error("Error during monthly birthday coupon assignment: {}", e.getMessage(), e);
        }
    }
}
