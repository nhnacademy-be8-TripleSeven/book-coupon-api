package com.nhnacademy.bookapi.controller.coupontest;

import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 생성
    @PostMapping
    public ResponseEntity<BaseCouponResponseDTO> createCoupon(@RequestBody CouponCreationRequestDTO request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }

    // 도서 쿠폰 생성
    @PostMapping("/books")
    public ResponseEntity<BookCouponResponseDTO> createBookCoupon(@RequestBody BookCouponCreationRequestDTO request) {
        return ResponseEntity.ok(couponService.createBookCoupon(request));
    }

    // 카테고리 쿠폰 생성
    @PostMapping("/categories")
    public ResponseEntity<CategoryCouponResponseDTO> createCategoryCoupon(@RequestBody CategoryCouponCreationRequestDTO request) {
        return ResponseEntity.ok(couponService.createCategoryCoupon(request));
    }

    // 쿠폰 발급
    @PatchMapping("/{couponId}/assign")
    public ResponseEntity<CouponAssignResponseDTO> assignCoupon(
            @PathVariable Long couponId,
            @RequestParam Long memberId) {
        return ResponseEntity.ok(couponService.assignCoupon(new CouponAssignRequestDTO(couponId, memberId)));
    }

    // 쿠폰 사용
    @PatchMapping("/{couponId}/use")
    public ResponseEntity<CouponUseResponseDTO> useCoupon(@PathVariable Long couponId) {
        return ResponseEntity.ok(couponService.useCoupon(couponId));
    }

    // 쿠폰 삭제
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    // 특정 회원의 모든 쿠폰 조회
    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<CouponDetailsDTO>> getAllCouponsByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(couponService.getAllCouponsByMemberId(memberId));
    }

    // 특정 회원의 미사용 쿠폰 조회
    @GetMapping("/members/{memberId}/unused")
    public ResponseEntity<List<CouponDetailsDTO>> getUnusedCouponsByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(couponService.getUnusedCouponsByMemberId(memberId));
    }

    // 특정 정책의 쿠폰 조회
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<List<CouponDetailsDTO>> getCouponsByPolicyId(@PathVariable Long policyId) {
        return ResponseEntity.ok(couponService.getCouponsByPolicyId(policyId));
    }
}