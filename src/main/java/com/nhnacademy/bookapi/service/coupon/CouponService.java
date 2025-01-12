
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

    void createCouponsInBulk(CouponBulkCreationRequestDTO request);

    CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request);

    void deleteCoupon(Long id);

    void expireCoupons();

    CouponUseResponseDTO useCoupon(Long userId, Long couponId, Long bookId);

    CouponUseResponseDTO useBaseCoupon(Long couponId);

    List<CouponDetailsDTO> getAllCouponsByMemberId(Long userId);

    List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long memberId);

    List<CouponDetailsDTO> getUsedCouponsByMemberId(Long memberId);

    List<CouponDetailsDTO> getCouponsByPolicyId(Long policyId);

    List<CouponDetailsDTO> getCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate);

    List<CouponDetailsDTO> getUsedCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate);

    List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request);

    void assignMonthlyBirthdayCoupons();

    CouponPolicyOrderResponseDTO getCouponPolicyByCouponId(Long couponId);

    List<CouponAssignResponseDTO> issueWelcomeCoupon(Long memberId);
}
