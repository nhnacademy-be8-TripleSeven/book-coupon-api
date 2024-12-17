package com.nhnacademy.bookapi.controller.coupon;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/coupon-policies")
@RequiredArgsConstructor
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    @Operation(summary = "쿠폰 정책 생성", description = "새로운 쿠폰 정책을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "쿠폰 정책 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터")
    })
    @PostMapping
    public ResponseEntity<CouponPolicyResponseDTO> createCouponPolicy(@RequestBody CouponPolicyRequestDTO request) {
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "쿠폰 정책 수정", description = "쿠폰 정책 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 정책 수정 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CouponPolicyResponseDTO> updateCouponPolicy(@PathVariable Long id, @RequestBody CouponPolicyRequestDTO request) {
        CouponPolicyResponseDTO response = couponPolicyService.updateCouponPolicy(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 정책 삭제", description = "특정 쿠폰 정책을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "쿠폰 정책 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCouponPolicy(@PathVariable Long id) {
        couponPolicyService.deleteCouponPolicy(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 쿠폰 정책 조회", description = "등록된 모든 쿠폰 정책을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 정책 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<List<CouponPolicyResponseDTO>> getAllCouponPolicies() {
        List<CouponPolicyResponseDTO> response = couponPolicyService.getAllCouponPolicies();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 정책 ID로 조회", description = "특정 ID의 쿠폰 정책을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 정책 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CouponPolicyResponseDTO> getCouponPolicyById(@PathVariable Long id) {
        CouponPolicyResponseDTO response = couponPolicyService.getCouponPolicyById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 정책 이름으로 조회", description = "특정 이름의 쿠폰 정책을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 정책 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<CouponPolicyResponseDTO> getCouponPolicyByName(@PathVariable String name) {
        CouponPolicyResponseDTO response = couponPolicyService.getCouponPolicyByName(name);
        return ResponseEntity.ok(response);
    }
}
