
package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyOrderResponseDTO;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;

import java.time.LocalDate;
import java.util.List;

public interface CouponService {

    BaseCouponResponseDTO createCoupon(CouponCreationRequestDTO couponCreationRequestDTO);

    BookCouponResponseDTO createBookCoupon(BookCouponCreationRequestDTO request);

    CategoryCouponResponseDTO createCategoryCoupon(CategoryCouponCreationRequestDTO request);

    Coupon createCouponBasedOnTarget(CouponCreationAndAssignRequestDTO request, CouponPolicy policy);

    BulkCouponCreationResponseDTO createCouponsInBulk(CouponBulkCreationRequestDTO request);

    void expireCoupons();

    CouponUseResponseDTO useCoupon(Long userId, Long couponId, Long bookId);

    CouponUseResponseDTO useBaseCoupon(Long couponId);

    List<CouponDetailsDTO> getCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate);

    List<CouponDetailsDTO> getUsedCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate);

    List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request);

    BulkAssignResponseDTO assignMonthlyBirthdayCoupons();

    CouponPolicyOrderResponseDTO getCouponPolicyByCouponId(Long couponId);

    List<CouponAssignResponseDTO> issueWelcomeCoupon(Long memberId);

    List<AvailableCouponResponseDTO> getAvailableCoupons(Long userId, List<Long> bookIds, Long amount);

    Long applyCoupon(Long couponId, Long paymentAmount);

    // 사용 가능성이 있는 미사용 코드
    void deleteCoupon(Long id);

    CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request);

}
