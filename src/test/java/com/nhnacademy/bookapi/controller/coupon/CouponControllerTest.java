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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CouponControllerTest {

    @InjectMocks
    private CouponController couponController;

    @Mock
    private CouponService couponService;

    @Mock
    private BookService bookService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CouponPolicyService couponPolicyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCouponsInBulk() {
        // Given
        CouponBulkCreationRequestDTO requestDTO = new CouponBulkCreationRequestDTO();
        BulkCouponCreationResponseDTO responseDTO = new BulkCouponCreationResponseDTO();

        when(couponService.createCouponsInBulk(any(CouponBulkCreationRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<BulkCouponCreationResponseDTO> response = couponController.createCouponsInBulk(requestDTO);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testCreateAndAssignCoupons() {
        // Given
        CouponCreationAndAssignRequestDTO requestDTO = new CouponCreationAndAssignRequestDTO();
        List<CouponAssignResponseDTO> responseList = Collections.emptyList();

        when(couponService.createAndAssignCoupons(any(CouponCreationAndAssignRequestDTO.class))).thenReturn(responseList);

        // When
        ResponseEntity<List<CouponAssignResponseDTO>> response = couponController.createAndAssignCoupons(requestDTO);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseList, response.getBody());
    }

    @Test
    void testSearchBooksForCoupon() {
        // Given
        String query = "test";
        List<BookSearchDTO> results = Collections.emptyList();

        when(bookService.searchBooksByName(query)).thenReturn(results);

        // When
        ResponseEntity<List<BookSearchDTO>> response = couponController.searchBooksForCoupon(query);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(results, response.getBody());
    }

    @Test
    void testSearchCategoriesForCoupon() {
        // Given
        String query = "test";
        List<CategorySearchDTO> results = Collections.emptyList();

        when(categoryService.searchCategoriesByName(query)).thenReturn(results);

        // When
        ResponseEntity<List<CategorySearchDTO>> response = couponController.searchCategoriesForCoupon(query);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(results, response.getBody());
    }

    @Test
    void testUseCouponForUser() {
        // Given
        Long userId = 1L;
        Long couponId = 2L;
        Long bookId = 3L;
        CouponUseResponseDTO responseDTO = new CouponUseResponseDTO();

        when(couponService.useCoupon(userId, couponId, bookId)).thenReturn(responseDTO);

        // When
        ResponseEntity<CouponUseResponseDTO> response = couponController.useCouponForUser(userId, couponId, bookId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetCouponsForUser() {
        // Given
        Long userId = 1L;
        List<CouponDetailsDTO> coupons = Collections.emptyList();

        when(couponService.getCouponsForUser(eq(userId), any(), any(), any())).thenReturn(coupons);

        // When
        ResponseEntity<List<CouponDetailsDTO>> response = couponController.getCouponsForUser(userId, null, null, null);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(coupons, response.getBody());
    }

    @Test
    void testGetUsedCouponsForUser() {
        // Given
        Long userId = 1L;
        List<CouponDetailsDTO> usedCoupons = Collections.emptyList();

        when(couponService.getUsedCouponsForUser(eq(userId), any(), any(), any())).thenReturn(usedCoupons);

        // When
        ResponseEntity<List<CouponDetailsDTO>> response = couponController.getUsedCouponsForUser(userId, null, null, null);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usedCoupons, response.getBody());
    }
//
//    @Test
//    void testGetAvailableCoupons() {
//        // Given
//        Long userId = 1L;
//        List<Long> bookIds = List.of(1L, 2L);
//        Long amount = 1000L;
//        List<AvailableCouponResponseDTO> availableCoupons = Collections.emptyList();
//
//        when(couponService.getAvailableCoupons(userId, bookIds, amount)).thenReturn(availableCoupons);
//
//        // When
//        ResponseEntity<List<AvailableCouponResponseDTO>> response = couponController.getAvailableCoupons(userId, bookIds, amount);
//
//        // Then
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(availableCoupons, response.getBody());
//    }

    @Test
    void testApplyCoupon() {
        // Given
        Long couponId = 1L;
        Long paymentAmount = 1000L;
        Long discountAmount = 200L;

        when(couponService.applyCoupon(couponId, paymentAmount)).thenReturn(discountAmount);

        // When
        ResponseEntity<Long> response = couponController.applyCoupon(couponId, paymentAmount);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(discountAmount, response.getBody());
    }

    @Test
    void testGetCouponPolicyByCouponId() {
        // Given
        Long couponId = 1L;
        CouponPolicyOrderResponseDTO responseDTO = new CouponPolicyOrderResponseDTO();

        when(couponService.getCouponPolicyByCouponId(couponId)).thenReturn(responseDTO);

        // When
        ResponseEntity<CouponPolicyOrderResponseDTO> response = couponController.getCouponPolicyByCouponId(couponId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }
    @Test
    void testUseCoupon() {
        // Given
        Long couponId = 1L;
        CouponUseResponseDTO responseDTO = new CouponUseResponseDTO();

        when(couponService.useBaseCoupon(couponId)).thenReturn(responseDTO);

        // When
        ResponseEntity<CouponUseResponseDTO> response = couponController.useCoupon(couponId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testIssueWelcomeCoupon() {
        // Given
        Long memberId = 1L;
        List<CouponAssignResponseDTO> responses = Collections.emptyList();

        when(couponService.issueWelcomeCoupon(memberId)).thenReturn(responses);

        // When
        ResponseEntity<List<CouponAssignResponseDTO>> response = couponController.issueWelcomeCoupon(memberId);


        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responses, response.getBody());
    }

    @Test
    void testSearchCouponPoliciesByNameTest() {
        // Given
        String query = "test";
        List<CouponPolicyResponseDTO> responseList = Collections.emptyList();

        when(couponPolicyService.searchCouponPoliciesByName(query)).thenReturn(responseList);

        // When
        ResponseEntity<List<CouponPolicyResponseDTO>> response = couponController.searchCouponPoliciesByNameTest(query);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseList, response.getBody());
    }

    @Test
    void testAssignCoupon() {
        // Given
        CouponAssignRequestDTO requestDTO = new CouponAssignRequestDTO();
        CouponAssignResponseDTO responseDTO = new CouponAssignResponseDTO();

        when(couponService.assignCoupon(any(CouponAssignRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<CouponAssignResponseDTO> response = couponController.assignCoupon(requestDTO);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testDeleteCoupon() {
        // Given
        Long id = 1L;

        doNothing().when(couponService).deleteCoupon(id);

        // When
        ResponseEntity<Void> response = couponController.deleteCoupon(id);

        // Then
        assertEquals(204, response.getStatusCodeValue());
    }
}
