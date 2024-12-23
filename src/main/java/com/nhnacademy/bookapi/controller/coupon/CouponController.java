package com.nhnacademy.bookapi.controller.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.service.coupon.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // **관리자 전용 API** //

    @Operation(summary = "쿠폰 생성", description = "새로운 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음")
    })
    @PostMapping("/admin/coupons")
    public ResponseEntity<BaseCouponResponseDTO> createCoupon(@RequestBody CouponCreationRequestDTO request) {
        BaseCouponResponseDTO response = couponService.createCoupon(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "도서 쿠폰 생성", description = "도서와 연관된 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "도서 쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "도서 또는 쿠폰 정책을 찾을 수 없음")
    })
    @PostMapping("/admin/coupons/book")
    public ResponseEntity<BookCouponResponseDTO> createBookCoupon(@RequestBody BookCouponCreationRequestDTO request) {
        BookCouponResponseDTO response = couponService.createBookCoupon(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "카테고리 쿠폰 생성", description = "카테고리와 연관된 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카테고리 쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 또는 쿠폰 정책을 찾을 수 없음")
    })
    @PostMapping("/admin/coupons/category")
    public ResponseEntity<CategoryCouponResponseDTO> createCategoryCoupon(@RequestBody CategoryCouponCreationRequestDTO request) {
        CategoryCouponResponseDTO response = couponService.createCategoryCoupon(request);
        return ResponseEntity.status(201).body(response);
    }

//    @Operation(summary = "쿠폰 발급", description = "특정 회원에게 쿠폰을 발급합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
//            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
//            @ApiResponse(responseCode = "400", description = "이미 발급된 쿠폰")
//    })
//    @PostMapping("/admin/coupons/assign")
//    public ResponseEntity<CouponAssignResponseDTO> assignCoupon(@RequestBody CouponAssignRequestDTO request) {
//        CouponAssignResponseDTO response = couponService.assignCoupon(request);
//        return ResponseEntity.ok(response);
//    }

    @Operation(summary = "쿠폰 삭제", description = "쿠폰을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "쿠폰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @DeleteMapping("/admin/coupons/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 쿠폰 정책의 쿠폰 조회", description = "특정 정책에 해당하는 모든 쿠폰을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음")
    })
    @GetMapping("/admin/coupons/policy/{policyId}")
    public ResponseEntity<List<CouponDetailsDTO>> getCouponsByPolicyId(@PathVariable Long policyId) {
        List<CouponDetailsDTO> response = couponService.getCouponsByPolicyId(policyId);
        return ResponseEntity.ok(response);
    }

    // **사용자 전용 API** //
//
//    @Operation(summary = "쿠폰 사용", description = "사용자가 쿠폰을 사용합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공"),
//            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
//    })
//    @PostMapping("/api/coupons/use/{id}")
//    public ResponseEntity<CouponUseResponseDTO> useCoupon(@PathVariable Long id) {
//        CouponUseResponseDTO response = couponService.useCoupon(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "책 쿠폰 사용", description = "사용자가 특정 도서에 쿠폰을 사용합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "책 쿠폰 사용 성공"),
//            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
//    })
//    @PostMapping("/api/coupons/use/{couponId}/book/{bookId}")
//    public ResponseEntity<CouponUseResponseDTO> useBookCoupon(@PathVariable Long couponId, @PathVariable Long bookId) {
//        CouponUseResponseDTO response = couponService.useBookCoupon(couponId, bookId);
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "카테고리 쿠폰 사용", description = "사용자가 특정 카테고리에 쿠폰을 사용합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "카테고리 쿠폰 사용 성공"),
//            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
//    })
//    @PostMapping("/api/coupons/use/{couponId}/category/{categoryId}")
//    public ResponseEntity<CouponUseResponseDTO> useCategoryCoupon(@PathVariable Long couponId, @PathVariable Long categoryId) {
//        CouponUseResponseDTO response = couponService.useCategoryCoupon(couponId, categoryId);
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "회원 쿠폰 조회", description = "특정 회원의 모든 쿠폰을 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "회원의 쿠폰 조회 성공"),
//            @ApiResponse(responseCode = "404", description = "해당 회원의 쿠폰을 찾을 수 없음")
//    })
//    @GetMapping("/api/coupons/member/{memberId}")
//    public ResponseEntity<List<CouponDetailsDTO>> getAllCouponsByMemberId(@PathVariable Long memberId) {
//        List<CouponDetailsDTO> response = couponService.getAllCouponsByMemberId(memberId);
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "회원 미사용 쿠폰 조회", description = "특정 회원의 미사용 쿠폰만 조회합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "회원의 미사용 쿠폰 조회 성공"),
//            @ApiResponse(responseCode = "404", description = "해당 회원의 미사용 쿠폰을 찾을 수 없음")
//    })
//    @GetMapping("/api/coupons/member/{memberId}/unused")
//    public ResponseEntity<List<CouponDetailsDTO>> getUnusedCouponsByMemberId(@PathVariable Long memberId) {
//        List<CouponDetailsDTO> response = couponService.getUnusedCouponsByMemberId(memberId);
//        return ResponseEntity.ok(response);
//    }


    @Operation(summary = "사용자 쿠폰 사용", description = "사용자가 본인의 쿠폰을 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공"),
            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
    })
    @PostMapping("/api/coupons/use/{couponId}")
    public ResponseEntity<CouponUseResponseDTO> useCouponForUser(
            @RequestHeader("X-User") Long userId,
            @PathVariable Long couponId) {
        CouponUseResponseDTO response = couponService.useCoupon(userId, couponId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 도서 쿠폰 사용", description = "사용자가 특정 도서에 본인의 쿠폰을 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "책 쿠폰 사용 성공"),
            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
    })
    @PostMapping("/api/coupons/use/{couponId}/book/{bookId}")
    public ResponseEntity<CouponUseResponseDTO> useBookCouponForUser(
            @RequestHeader("X-User") Long userId,
            @PathVariable Long couponId,
            @PathVariable Long bookId) {
        CouponUseResponseDTO response = couponService.useBookCoupon(userId, couponId, bookId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 카테고리 쿠폰 사용", description = "사용자가 특정 카테고리에 본인의 쿠폰을 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 쿠폰 사용 성공"),
            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
    })
    @PostMapping("/api/coupons/use/{couponId}/category/{categoryId}")
    public ResponseEntity<CouponUseResponseDTO> useCategoryCouponForUser(
            @RequestHeader("X-User") Long userId,
            @PathVariable Long couponId,
            @PathVariable Long categoryId) {
        CouponUseResponseDTO response = couponService.useCategoryCoupon(userId, couponId, categoryId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 쿠폰 조회", description = "사용자가 본인의 모든 쿠폰을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons")
    public ResponseEntity<List<CouponDetailsDTO>> getCouponsForUser(@RequestHeader("X-User") Long userId) {
        List<CouponDetailsDTO> response = couponService.getAllCouponsByMemberId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 미사용 쿠폰 조회", description = "사용자가 본인의 미사용 쿠폰만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미사용 쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "미사용 쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/unused")
    public ResponseEntity<List<CouponDetailsDTO>> getUnusedCouponsForUser(@RequestHeader("X-User") Long userId) {
        List<CouponDetailsDTO> response = couponService.getUnusedCouponsByMemberId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 사용 쿠폰 조회", description = "사용자가 본인의 사용 쿠폰만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용 쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/used")
    public ResponseEntity<List<CouponDetailsDTO>> getUsedCouponsForUser(@RequestHeader("X-User") Long userId) {
        List<CouponDetailsDTO> response = couponService.getUsedCouponsByMemberId(userId);
        return ResponseEntity.ok(response);
    }


    // 미구현

    @Operation(summary = "결제 시 사용 가능한 쿠폰 조회", description = "사용 가능한 쿠폰 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/available")
    public ResponseEntity<Void> getAvailableCoupons(@RequestHeader("X-User") Long userId) {
        return ResponseEntity.ok().build();
    }

    // 생일 쿠폰 자동 생성
    @Operation(summary = "생일 쿠폰 생성", description = "회원의 생일에 자동으로 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생일 쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
    })
    @PostMapping("/api/coupons/birthday")
    public ResponseEntity<Void> generateBirthdayCoupon(@RequestHeader("X-User") Long userId) {
        return ResponseEntity.ok().build();
    }

    // 회원 가입시 쿠폰 자동 생성
    @Operation(summary = "welcome 쿠폰 생성", description = "회원가입시 자동으로 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "welcome 쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
    })
    @PostMapping("/api/coupons/welcome")
    public ResponseEntity<Void> generateWelcomeCoupon(@RequestHeader("X-User") Long userId) {
        return ResponseEntity.ok().build();
    }
}
