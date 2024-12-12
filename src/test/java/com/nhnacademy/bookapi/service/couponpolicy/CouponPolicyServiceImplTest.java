package com.nhnacademy.bookapi.service.couponpolicy;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.exception.CouponMinBiggerThanMaxException;
import com.nhnacademy.bookapi.exception.CouponPolicyNotFoundException;
import com.nhnacademy.bookapi.exception.DiscountRateAndAmountException;
import com.nhnacademy.bookapi.repository.CouponPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponPolicyServiceImplTest {

    @InjectMocks
    private CouponPolicyServiceImpl couponPolicyService;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCouponPolicy() {
        // Given
        CouponPolicy mockPolicy = new CouponPolicy();
        mockPolicy.setName("Test Policy");
        mockPolicy.setCouponDiscountRate(new BigDecimal("0.15"));
        mockPolicy.setCouponValidTime(30);

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> {
            CouponPolicy policy = invocation.getArgument(0);
            policy.setTestId(1L);
            return policy;
        });

        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Test Policy",
                1000L,
                5000L,
                new BigDecimal("0.15"),
                0L,
                30
        );

        // When
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Policy", response.getName());
        assertEquals(new BigDecimal("0.15"), response.getCouponDiscountRate());
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    @Test
    void testUpdateCouponPolicy_Success() {
        // Given
        CouponPolicy existingPolicy = new CouponPolicy();
        existingPolicy.setName("Old Policy");

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> {
            CouponPolicy savedPolicy = invocation.getArgument(0);
            savedPolicy.setTestId(1L);
            return savedPolicy;
        });

        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Updated Policy",
                2000L,
                6000L,
                new BigDecimal("0"),
                1000L,
                45
        );

        // When
        CouponPolicyResponseDTO response = couponPolicyService.updateCouponPolicy(1L, request);

        // Then
        assertNotNull(response);
        assertEquals("Updated Policy", response.getName());
        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("0"), response.getCouponDiscountRate());
        assertEquals(1000L, response.getCouponDiscountAmount());
        verify(couponPolicyRepository, times(1)).findById(1L);
        verify(couponPolicyRepository, times(1)).save(existingPolicy);
    }


    @Test
    void testUpdateCouponPolicy_NotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Updated Policy",
                2000L,
                6000L,
                BigDecimal.ZERO,
                1500L,
                45
        );

        // Then
        assertThrows(CouponPolicyNotFoundException.class, () ->
                couponPolicyService.updateCouponPolicy(1L, request));
        verify(couponPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteCouponPolicy_Success() {
        // Given
        when(couponPolicyRepository.existsById(1L)).thenReturn(true);

        // When
        couponPolicyService.deleteCouponPolicy(1L);

        // Then
        verify(couponPolicyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCouponPolicy_NotFound() {
        // Given
        when(couponPolicyRepository.existsById(1L)).thenReturn(false);

        // Then
        assertThrows(CouponPolicyNotFoundException.class, () ->
                couponPolicyService.deleteCouponPolicy(1L));
        verify(couponPolicyRepository, times(1)).existsById(1L);
    }

    @Test
    void testGetAllCouponPolicies() {
        // Given
        CouponPolicy policy1 = new CouponPolicy();
        policy1.setName("Policy 1");

        CouponPolicy policy2 = new CouponPolicy();
        policy2.setName("Policy 2");

        when(couponPolicyRepository.findAll()).thenReturn(Arrays.asList(policy1, policy2));

        // When
        List<CouponPolicyResponseDTO> responseList = couponPolicyService.getAllCouponPolicies();

        // Then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        verify(couponPolicyRepository, times(1)).findAll();
    }

    @Test
    void testGetCouponPolicyById_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy();
        policy.setTestId(1L);
        policy.setName("Policy 1");
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // When
        CouponPolicyResponseDTO response = couponPolicyService.getCouponPolicyById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Policy 1", response.getName());
        verify(couponPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCouponPolicyById_NotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(CouponPolicyNotFoundException.class, () ->
                couponPolicyService.getCouponPolicyById(1L));
        verify(couponPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCouponPolicyByName_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy();
        policy.setName("Policy 1");
        when(couponPolicyRepository.findByName("Policy 1")).thenReturn(Optional.of(policy));

        // When
        CouponPolicyResponseDTO response = couponPolicyService.getCouponPolicyByName("Policy 1");

        // Then
        assertNotNull(response);
        assertEquals("Policy 1", response.getName());
        verify(couponPolicyRepository, times(1)).findByName("Policy 1");
    }

    @Test
    void testGetCouponPolicyByName_NotFound() {
        // Given
        when(couponPolicyRepository.findByName("Policy 1")).thenReturn(Optional.empty());

        // Then
        assertThrows(CouponPolicyNotFoundException.class, () ->
                couponPolicyService.getCouponPolicyByName("Policy 1"));
        verify(couponPolicyRepository, times(1)).findByName("Policy 1");
    }

    @Test
    void testValidateCouponPolicyRequest_MinGreaterThanMax_ExceptionThrown() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Invalid Policy",
                5000L, // Minimum amount
                1000L, // Maximum amount
                new BigDecimal("0.1"),
                0L,
                30
        );

        // Then
        assertThrows(CouponMinBiggerThanMaxException.class,
                () -> couponPolicyService.createCouponPolicy(request));
    }

    @Test
    void testValidateCouponPolicyRequest_BothDiscountRateAndAmountZero_ExceptionThrown() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Invalid Policy",
                1000L,
                5000L,
                BigDecimal.ZERO, // Discount rate
                0L,              // Discount amount
                30
        );

        // Then
        assertThrows(DiscountRateAndAmountException.class,
                () -> couponPolicyService.createCouponPolicy(request));
    }

    @Test
    void testValidateCouponPolicyRequest_BothDiscountRateAndAmountNonZero_ExceptionThrown() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Invalid Policy",
                1000L,
                5000L,
                new BigDecimal("0.2"),
                1000L,
                30
        );

        // Then
        assertThrows(DiscountRateAndAmountException.class,
                () -> couponPolicyService.createCouponPolicy(request));
    }
}
