package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.client.MemberFeignClient;
import com.nhnacademy.bookapi.dto.coupon.CouponAssignResponseDTO;
import com.nhnacademy.bookapi.dto.coupon.CouponCreationAndAssignRequestDTO;
import com.nhnacademy.bookapi.dto.member.MemberNotFoundException;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.exception.CouponPolicyNotFoundException;
import com.nhnacademy.bookapi.repository.CouponPolicyRepository;
import com.nhnacademy.bookapi.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponHelperServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponHelperService couponHelperService;

    @Mock
    private MemberFeignClient memberFeignClient;

    @Test
    void testCreateAndAssignCoupons_Success() {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .couponValidTime(30)
                .build();

        List<Long> memberIds = List.of(1L, 2L);

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponService.getMemberIdsByRecipientType(any())).thenReturn(memberIds);
        when(couponService.createCouponBasedOnTarget(any(), any())).thenAnswer(invocation -> {
            CouponCreationAndAssignRequestDTO request = invocation.getArgument(0);
            return Coupon.builder()
                    .id(1L)
                    .name(request.getName())
                    .couponPolicy(policy)
                    .build();
        });
        when(couponRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "전체"
        );

        List<CouponAssignResponseDTO> response = couponHelperService.createAndAssignCoupons(request);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getCouponId());
    }

    @Test
    void testCreateAndAssignCoupons_Failure_EmptyRecipients() {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponService.getMemberIdsByRecipientType(any())).thenReturn(List.of());

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "전체"
        );

        assertThrows(MemberNotFoundException.class, () -> couponHelperService.createAndAssignCoupons(request));
    }



    @Test
    void testCreateAndAssignCoupons_Failure_CouponPolicyNotFound() {
        // Arrange
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "개인별"
        );

        // Act & Assert
        assertThrows(CouponPolicyNotFoundException.class,
                () -> couponHelperService.createAndAssignCoupons(request));
    }

    @Test
    void testCreateAndAssignCoupons_Success_IndividualRecipient() {
        // Arrange
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .couponValidTime(30)
                .build();

        Long memberId = 1L;

        // Mock된 Coupon 객체 생성
        Coupon mockCoupon = Coupon.builder()
                .id(1L)
                .name("Test Coupon")
                .couponPolicy(policy)
                .build();

        // Mock 설정
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponService.getMemberIdsByRecipientType(any(CouponCreationAndAssignRequestDTO.class)))
                .thenReturn(List.of(memberId));
        when(couponService.createCouponBasedOnTarget(any(CouponCreationAndAssignRequestDTO.class), any(CouponPolicy.class)))
                .thenReturn(mockCoupon);

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                List.of(memberId),
                "개인별"
        );

        // Act
        List<CouponAssignResponseDTO> response = couponHelperService.createAndAssignCoupons(request);

        // Assert
        assertNotNull(response);
    }


    @Test
    void testCreateAndAssignCoupons_Success_GradeRecipient() {
        // Arrange
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .couponValidTime(30)
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Test Coupon")
                .couponPolicy(policy)
                .build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponService.getMemberIdsByRecipientType(any(CouponCreationAndAssignRequestDTO.class)))
                .thenReturn(List.of(1L));
        when(couponService.createCouponBasedOnTarget(any(CouponCreationAndAssignRequestDTO.class), eq(policy)))
                .thenReturn(coupon);
        when(couponRepository.saveAll(anyList())).thenReturn(List.of(coupon));

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                List.of(1L),
                "등급별"
        );

        // Act
        List<CouponAssignResponseDTO> response = couponHelperService.createAndAssignCoupons(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getCouponId());
    }
}
