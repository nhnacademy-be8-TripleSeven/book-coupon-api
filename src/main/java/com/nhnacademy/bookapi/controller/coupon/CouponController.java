package com.nhnacademy.bookapi.controller.coupon;

import com.nhnacademy.bookapi.dto.book.BookSearchDTO;
import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
import com.nhnacademy.bookapi.dto.coupon.*;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyOrderResponseDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.coupon.CouponService;
import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    private final BookService bookService;

    private final CategoryService categoryService;

    private final CouponPolicyService couponPolicyService;



    // **관리자 전용 API** //
    @Operation(summary = "단체 쿠폰 생성", description = "다수의 쿠폰을 한 번에 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/admin/coupons/bulk")
    public ResponseEntity<BulkCouponCreationResponseDTO> createCouponsInBulk(@RequestBody CouponBulkCreationRequestDTO request) {

        BulkCouponCreationResponseDTO responseDTO = couponService.createCouponsInBulk(request);

        return ResponseEntity.status(201).body(responseDTO);
    }


    @Operation(summary = "쿠폰 생성 및 발급", description = "쿠폰을 생성한 후 대상 회원들에게 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 생성 및 발급 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/admin/coupons/create-and-assign")
    public ResponseEntity<List<CouponAssignResponseDTO>> createAndAssignCoupons(
            @RequestBody CouponCreationAndAssignRequestDTO request) {
        List<CouponAssignResponseDTO> responses = couponService.createAndAssignCoupons(request);
        return ResponseEntity.ok(responses);
    }

    // 쿠폰 대상 지정을 위한 도서 검색
    @GetMapping("/admin/coupons/book-search")
    public ResponseEntity<List<BookSearchDTO>> searchBooksForCoupon(@RequestParam("query") String query) {
        List<BookSearchDTO> results = bookService.searchBooksByName(query);
        return ResponseEntity.ok(results);
    }

    // 쿠폰 대상 지정을 위한 카테고리 검색
    @GetMapping("/admin/coupons/category-search")
    public ResponseEntity<List<CategorySearchDTO>> searchCategoriesForCoupon(@RequestParam("query") String query) {
        List<CategorySearchDTO> results = categoryService.searchCategoriesByName(query);
        return ResponseEntity.ok(results);
    }








    // **멤버 전용 API** //
    @Operation(summary = "사용자 쿠폰 사용", description = "사용자가 본인의 쿠폰을 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공"),
            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
    })
    @PostMapping("/api/coupons/use")
    public ResponseEntity<CouponUseResponseDTO> useCouponForUser(
            @RequestHeader("X-USER") Long userId,
            @RequestParam Long couponId,
            @RequestParam(required = false) Long bookId ) {
        CouponUseResponseDTO response = couponService.useCoupon(userId, couponId, bookId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 쿠폰 조회", description = "사용자가 본인의 모든 쿠폰을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons")
    public ResponseEntity<List<CouponDetailsDTO>> getCouponsForUser(@RequestHeader("X-USER") Long userId,
                                                                    @RequestParam(required = false) String keyword,
                                                                    @RequestParam(required = false) LocalDate startDate,
                                                                    @RequestParam(required = false) LocalDate endDate) {
        List<CouponDetailsDTO> coupons = couponService.getCouponsForUser(userId, keyword, startDate, endDate);
        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "사용자 사용 쿠폰 조회", description = "사용자가 본인의 사용한 쿠폰만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용 쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/used")
    public ResponseEntity<List<CouponDetailsDTO>> getUsedCouponsForUser(
            @RequestHeader(value = "X-USER") Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<CouponDetailsDTO> usedCoupons = couponService.getUsedCouponsForUser(userId, keyword, startDate, endDate);
        return ResponseEntity.ok(usedCoupons);
    }

    @Operation(summary = "결제 시 사용 가능한 쿠폰 조회", description = "사용 가능한 쿠폰 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/available")
    public ResponseEntity<List<AvailableCouponResponseDTO>> getAvailableCoupons(
            @RequestHeader("X-USER") Long userId,
            @RequestParam List<Long> bookId,
            @RequestParam Long amount) {

        List<AvailableCouponResponseDTO> availableCoupons = couponService.getAvailableCoupons(userId, bookId, amount);

        return ResponseEntity.ok(availableCoupons);
    }










    @Operation(summary = "쿠폰 적용 후 할인 금액 계산", description = "쿠폰을 적용하여 할인되는 금액 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "할인 금액 계산 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청")
    })
    @PostMapping("/coupons/apply")
    public ResponseEntity<Long> applyCoupon(
            @RequestParam Long couponId,
            @RequestParam Long paymentAmount) {

        Long discountAmount = couponService.applyCoupon(couponId, paymentAmount);
        return ResponseEntity.ok(discountAmount);
    }

    @Operation(summary = "무인증 쿠폰 사용", description = "인증없이 쿠폰을 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공"),
            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
    })
    @PostMapping("/coupons/use/{couponId}")
    public ResponseEntity<CouponUseResponseDTO> useCoupon(
            @PathVariable Long couponId) {
        CouponUseResponseDTO response = couponService.useBaseCoupon(couponId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 아이디 기반 쿠폰 정책 조회", description = "쿠폰 아이디를 기반으로 쿠폰 정책을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 정책 조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 아이디나 쿠폰 정책을 찾을 수 없음")
    })
    @GetMapping("/coupons/{couponId}/coupon-polities")
    public ResponseEntity<CouponPolicyOrderResponseDTO> getCouponPolicyByCouponId(
            @PathVariable Long couponId) {
        CouponPolicyOrderResponseDTO response = couponService.getCouponPolicyByCouponId(couponId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "회원가입 Welcome 쿠폰 발급",
            description = "회원가입 성공 시 'Welcome' 정책에 해당하는 쿠폰을 생성 및 발급하며, 추가적으로 '회원가입 선착순 쿠폰'이 있다면 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰 정책 또는 사용 가능한 쿠폰 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/coupons/create/welcome")
    public ResponseEntity<List<CouponAssignResponseDTO>> issueWelcomeCoupon(@RequestParam Long memberId) {
        List<CouponAssignResponseDTO> responses = couponService.issueWelcomeCoupon(memberId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "쿠폰 정책 이름 검색", description = "입력한 이름을 포함하는 쿠폰 정책을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 정책 검색 성공"),
            @ApiResponse(responseCode = "404", description = "해당 이름의 쿠폰 정책을 찾을 수 없음")
    })
    @GetMapping("/coupon-policies/search")
    public ResponseEntity<List<CouponPolicyResponseDTO>> searchCouponPoliciesByNameTest(@RequestParam String query) {
        List<CouponPolicyResponseDTO> response = couponPolicyService.searchCouponPoliciesByName(query);
        return ResponseEntity.ok(response);
    }








    // **미사용, but 사용 가능성 있음 API** //
    @Operation(summary = "쿠폰 발급", description = "특정 회원에게 쿠폰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 발급된 쿠폰")
    })
    @PostMapping("/admin/coupons/assign")
    public ResponseEntity<CouponAssignResponseDTO> assignCoupon(@RequestBody CouponAssignRequestDTO request) {
        CouponAssignResponseDTO response = couponService.assignCoupon(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 단일 삭제", description = "쿠폰을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "쿠폰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @DeleteMapping("/admin/coupons/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

}
