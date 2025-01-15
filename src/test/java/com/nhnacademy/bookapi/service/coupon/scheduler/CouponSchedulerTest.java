package com.nhnacademy.bookapi.service.coupon.scheduler;

import com.nhnacademy.bookapi.service.coupon.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class CouponSchedulerTest {

    @InjectMocks
    private CouponScheduler couponScheduler;

    @Mock
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessExpiredCoupons() {
        // Given: CouponService의 expireCoupons 메서드가 정상 호출될 것으로 기대
        doNothing().when(couponService).expireCoupons();

        // When: 스케줄러의 메서드를 직접 호출
        couponScheduler.processExpiredCoupons();

        // Then: expireCoupons 메서드가 호출되었는지 검증
        verify(couponService, times(1)).expireCoupons();
    }

    @Test
    void testAssignMonthlyBirthdayCoupons_Success() {
        // Given
        when(couponService.assignMonthlyBirthdayCoupons()).thenReturn(null);

        // When
        couponScheduler.assignMonthlyBirthdayCoupons();

        // Then
        verify(couponService, times(1)).assignMonthlyBirthdayCoupons();
    }


    @Test
    void testAssignMonthlyBirthdayCoupons_Exception() {
        // Given: CouponService 메서드 호출 시 예외 발생
        doThrow(new RuntimeException("Test exception"))
                .when(couponService).assignMonthlyBirthdayCoupons();

        // When: 스케줄러의 메서드를 호출
        couponScheduler.assignMonthlyBirthdayCoupons();

        // Then: 예외가 로깅되고 정상적으로 처리되었는지 확인
        verify(couponService, times(1)).assignMonthlyBirthdayCoupons();
    }
}
