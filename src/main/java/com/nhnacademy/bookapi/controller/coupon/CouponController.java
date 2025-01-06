package com.nhnacademy.bookapi.controller.coupon;

import com.nhnacademy.bookapi.dto.book.BookSearchDTO;
import com.nhnacademy.bookapi.dto.category.CategorySearchDTO;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.coupon.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "쿠폰 발급", description = "특정 회원에게 쿠폰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 발급된 쿠폰"),
            @ApiResponse(responseCode = "502", description = "RabbitMQ 통신 오류"),
            @ApiResponse(responseCode = "503", description = "RabbitMQ 서비스 불가")
    })
    @PostMapping("/admin/coupons/assign")
    public ResponseEntity<CouponAssignResponseDTO> assignCoupon(@RequestBody CouponAssignRequestDTO request) {
        CouponAssignResponseDTO response = couponService.assignCoupon(request);
        return ResponseEntity.ok(response);
    }

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

//    // 쿠폰 대상 지정을 위한 카테고리 검색
//    @GetMapping("/admin/coupons/category-search")
//    public ResponseEntity<List<CategorySearchDTO>> searchCategoriesForCoupon(@RequestParam("query") String query) {
//        List<CategorySearchDTO> results = categoryService.searchCategoriesByName(query);
//        return ResponseEntity.ok(results);
//    }








    // 멤버 전용 api
    @Operation(summary = "사용자 쿠폰 사용", description = "사용자가 본인의 쿠폰을 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공"),
            @ApiResponse(responseCode = "400", description = "사용할 수 없는 쿠폰")
    })
    @PostMapping("/api/coupons/use/{couponId}")
    public ResponseEntity<CouponUseResponseDTO> useCouponForUser(
            @RequestHeader("X-USER") Long userId,
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
            @RequestHeader("X-USER") Long userId,
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


    @Operation(summary = "사용자 미사용 쿠폰 조회", description = "사용자가 본인의 미사용 쿠폰만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미사용 쿠폰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "미사용 쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/unused")
    public ResponseEntity<List<CouponDetailsDTO>> getUnUsedCouponsForUser(
            @RequestHeader(value = "X-USER") Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<CouponDetailsDTO> usedCoupons = couponService.getUsedCouponsForUser(userId, keyword, startDate, endDate);
        return ResponseEntity.ok(usedCoupons);
    }


    @Operation(summary = "쿠폰 사용", description = "쿠폰을 사용합니다.")
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



    // 회원가입 시 Welcome 쿠폰 자동 생성
    @Operation(summary = "welcome 쿠폰 생성", description = "회원가입시 자동으로 쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "welcome 쿠폰 생성 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
    })
    @PostMapping("/coupons/create/welcome")
    public ResponseEntity<List<CouponAssignResponseDTO>> issueWelcomeCoupon(@RequestParam Long memberId) {
        // 쿠폰 생성 및 발급 요청 DTO 생성
        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO("Welcome Coupon", 38L, Collections.singletonList(memberId), "개인별");
        // 쿠폰 발급 서비스 호출
        List<CouponAssignResponseDTO> responses = couponService.createAndAssignCoupons(request);
        return ResponseEntity.ok(responses);
    }







    // 미구현
    @Operation(summary = "결제 시 사용 가능한 쿠폰 조회", description = "사용 가능한 쿠폰 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @GetMapping("/api/coupons/available")
    public ResponseEntity<Void> getAvailableCoupons(@RequestHeader("X-USER") Long userId) {
        return ResponseEntity.ok().build();
    }

}
