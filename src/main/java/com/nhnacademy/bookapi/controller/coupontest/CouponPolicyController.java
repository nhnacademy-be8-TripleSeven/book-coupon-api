//package com.nhnacademy.bookapi.controller.coupontest;
//
//import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
//import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
//import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/coupon-policies")
//@RequiredArgsConstructor
//public class CouponPolicyController {
//
//    private final CouponPolicyService couponPolicyService;
//
//    // 쿠폰 정책 생성
//    @PostMapping
//    public ResponseEntity<CouponPolicyResponseDTO> createCouponPolicy(@RequestBody CouponPolicyRequestDTO request) {
//        return ResponseEntity.ok(couponPolicyService.createCouponPolicy(request));
//    }
//
//    // 쿠폰 정책 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<CouponPolicyResponseDTO> updateCouponPolicy(
//            @PathVariable Long id,
//            @RequestBody CouponPolicyRequestDTO request) {
//        return ResponseEntity.ok(couponPolicyService.updateCouponPolicy(id, request));
//    }
//
//    // 쿠폰 정책 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCouponPolicy(@PathVariable Long id) {
//        couponPolicyService.deleteCouponPolicy(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // 모든 쿠폰 정책 조회
//    @GetMapping
//    public ResponseEntity<List<CouponPolicyResponseDTO>> getAllCouponPolicies() {
//        return ResponseEntity.ok(couponPolicyService.getAllCouponPolicies());
//    }
//
//    // 쿠폰 정책 ID로 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<CouponPolicyResponseDTO> getCouponPolicyById(@PathVariable Long id) {
//        return ResponseEntity.ok(couponPolicyService.getCouponPolicyById(id));
//    }
//
//    // 쿠폰 정책 이름으로 조회
//    @GetMapping("/search")
//    public ResponseEntity<CouponPolicyResponseDTO> getCouponPolicyByName(@RequestParam String name) {
//        return ResponseEntity.ok(couponPolicyService.getCouponPolicyByName(name));
//    }
//}
