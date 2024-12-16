
package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;

import java.util.List;

public interface CouponService {

    BaseCouponResponseDTO createCoupon(CouponCreationRequestDTO couponCreationRequestDTO);

    BookCouponResponseDTO createBookCoupon(BookCouponCreationRequestDTO request);

    CategoryCouponResponseDTO createCategoryCoupon(CategoryCouponCreationRequestDTO request);

    CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request);

    CouponUseResponseDTO useCoupon(Long id);

    CouponUseResponseDTO useBookCoupon(Long couponId, Long bookId);

    CouponUseResponseDTO useCategoryCoupon(Long couponId, Long categoryId);

    void deleteCoupon(Long id);

    void expireCoupons();

    List<CouponDetailsDTO> getAllCouponsByMemberId(Long memberId);

    List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long memberId);

    List<CouponDetailsDTO> getCouponsByPolicyId(Long policyId);

}
