
package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;

import java.util.List;

public interface CouponService {

    BaseCouponResponseDTO createCoupon(CouponCreationRequestDTO couponCreationRequestDTO);

    BookCouponResponseDTO createBookCoupon(BookCouponCreationRequestDTO request);

    CategoryCouponResponseDTO createCategoryCoupon(CategoryCouponCreationRequestDTO request);

//    CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request);

    void deleteCoupon(Long id);

    void expireCoupons();

//    CouponUseResponseDTO useCoupon(Long id);
//
//    CouponUseResponseDTO useBookCoupon(Long couponId, Long bookId);
//
//    CouponUseResponseDTO useCategoryCoupon(Long couponId, Long categoryId);
//
//    List<CouponDetailsDTO> getAllCouponsByMemberId(Long memberId);
//
//    List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long memberId);

    CouponUseResponseDTO useCoupon(Long userId, Long couponId);

    CouponUseResponseDTO useBookCoupon(Long userId, Long couponId, Long bookId);

    CouponUseResponseDTO useCategoryCoupon(Long userId, Long couponId, Long categoryId);

    List<CouponDetailsDTO> getAllCouponsByMemberId(Long userId);

    List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long memberId);

    List<CouponDetailsDTO> getUsedCouponsByMemberId(Long memberId);

    List<CouponDetailsDTO> getCouponsByPolicyId(Long policyId);

}
