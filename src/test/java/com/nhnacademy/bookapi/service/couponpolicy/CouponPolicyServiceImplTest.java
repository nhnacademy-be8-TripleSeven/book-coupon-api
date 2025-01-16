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
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Test Policy",
                1000L,
                5000L,
                new BigDecimal("0.15"),
                0L,
                30
        );

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> {
            CouponPolicy policy = invocation.getArgument(0);
            return new CouponPolicy(
                    "Test Policy",
                    1000L,
                    5000L,
                    new BigDecimal("0.15"),
                    null,
                    30
            );
        });

        // When
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);

        // Then
        assertNotNull(response);
        assertEquals("Test Policy", response.getName());
        assertEquals(new BigDecimal("0.15"), response.getCouponDiscountRate());
    }

    @Test
    void testCreateCouponPolicy_ValidWithDiscountRate() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Discount Rate Policy",
                1000L,
                5000L,
                new BigDecimal("0.1"),
                null,
                30
        );

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> {
            CouponPolicy policy = invocation.getArgument(0);
            return policy;
        });

        // When
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);

        // Then
        assertNotNull(response);
        assertEquals("Discount Rate Policy", response.getName());
        assertEquals(new BigDecimal("0.1"), response.getCouponDiscountRate());
    }

    @Test
    void testCreateCouponPolicy_ValidWithDiscountAmount() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Discount Amount Policy",
                1000L,
                5000L,
                null,
                1000L,
                30
        );

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> {
            CouponPolicy policy = invocation.getArgument(0);
            return policy;
        });

        // When
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);

        // Then
        assertNotNull(response);
        assertEquals("Discount Amount Policy", response.getName());
        assertEquals(1000L, response.getCouponDiscountAmount());
    }


    @Test
    void testUpdateCouponPolicy_Success() {
        // Given
        CouponPolicy existingPolicy = new CouponPolicy(
                "Old Policy",
                1000L,
                5000L,
                BigDecimal.ZERO,
                100L,
                30
        );
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> {
            CouponPolicy savedPolicy = invocation.getArgument(0);
            savedPolicy.builder().id(1L).build();
            return savedPolicy;
        });

        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Updated Policy",
                2000L,
                6000L,
                null,
                1000L,
                45
        );

        // When
        CouponPolicyResponseDTO response = couponPolicyService.updateCouponPolicy(1L, request);

        // Then
        assertNotNull(response);
        assertEquals("Updated Policy", response.getName());
        assertEquals(2000L, response.getCouponMinAmount());
        assertEquals(6000L, response.getCouponMaxAmount());
    }


    @Test
    void testUpdateCouponPolicy_NotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Updated Policy",
                2000L,
                6000L,
                null,
                1500L,
                45
        );

        // Then
        assertThrows(CouponPolicyNotFoundException.class, () ->
                couponPolicyService.updateCouponPolicy(1L, request));
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
        CouponPolicy policy1 = new CouponPolicy(
                "Policy 1",
                1000L,
                5000L,
                null,
                100L,
                30
        );

        CouponPolicy policy2 = new CouponPolicy(
                "Policy 2",
                2000L,
                6000L,
                new BigDecimal("0.2"),
                null,
                45
        );

        when(couponPolicyRepository.findAll()).thenReturn(Arrays.asList(policy1, policy2));

        // When
        List<CouponPolicyResponseDTO> responseList = couponPolicyService.getAllCouponPolicies();

        // Then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
    }

    @Test
    void testGetAllCouponPolicies_Empty() {
        // Given
        when(couponPolicyRepository.findAll()).thenReturn(List.of());

        // Then
        assertThrows(CouponPolicyNotFoundException.class,
                () -> couponPolicyService.getAllCouponPolicies());
    }

    @Test
    void testGetCouponPolicyById_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy(
                "Policy 1",
                1000L,
                5000L,
                null,
                100L,
                30
        );
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // When
        CouponPolicyResponseDTO response = couponPolicyService.getCouponPolicyById(1L);

        // Then
        assertNotNull(response);
        assertEquals("Policy 1", response.getName());
    }

    @Test
    void testGetCouponPolicyById_NotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(CouponPolicyNotFoundException.class, () ->
                couponPolicyService.getCouponPolicyById(1L));
    }

    @Test
    void testGetCouponPolicyByName_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy(
                "Policy 1",
                1000L,
                5000L,
                null,
                100L,
                30
        );
        when(couponPolicyRepository.findByName("Policy 1")).thenReturn(Optional.of(policy));

        // When
        CouponPolicyResponseDTO response = couponPolicyService.getCouponPolicyByName("Policy 1");

        // Then
        assertNotNull(response);
        assertEquals("Policy 1", response.getName());
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
    void testValidateCouponPolicyRequest_DiscountAmountZero_Valid() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Valid Policy with Zero Discount Amount",
                1000L,
                5000L,
                new BigDecimal("0.2"),
                null,
                30
        );

        // Mock
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);

        // Then
        assertNotNull(response);
        assertEquals("Valid Policy with Zero Discount Amount", response.getName());
    }

    @Test
    void testValidateCouponPolicyRequest_DiscountAmountNonZero_Valid() {
        // Given
        CouponPolicyRequestDTO request = new CouponPolicyRequestDTO(
                "Valid Policy with Non-Zero Discount Amount",
                1000L,
                5000L,
                null,
                2000L,
                30
        );

        // Mock
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CouponPolicyResponseDTO response = couponPolicyService.createCouponPolicy(request);

        // Then
        assertNotNull(response);
        assertEquals("Valid Policy with Non-Zero Discount Amount", response.getName());
        assertEquals(2000L, response.getCouponDiscountAmount());
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


    @Test
    void testSearchCouponPoliciesByName_Success() {
        // Given
        CouponPolicy policy1 = new CouponPolicy(
                "Policy A",
                1000L,
                5000L,
                null,
                100L,
                30
        );
        CouponPolicy policy2 = new CouponPolicy(
                "Policy B",
                2000L,
                6000L,
                new BigDecimal("0.2"),
                null,
                45
        );

        when(couponPolicyRepository.findByNameContainingIgnoreCase("Policy"))
                .thenReturn(Arrays.asList(policy1, policy2));

        // When
        List<CouponPolicyResponseDTO> responseList = couponPolicyService.searchCouponPoliciesByName("Policy");

        // Then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("Policy A", responseList.get(0).getName());
        assertEquals("Policy B", responseList.get(1).getName());
    }

    @Test
    void testSearchCouponPoliciesByName_NotFound() {
        // Given
        when(couponPolicyRepository.findByNameContainingIgnoreCase("NonExistentPolicy"))
                .thenReturn(List.of());

        // Then
        assertThrows(CouponPolicyNotFoundException.class,
                () -> couponPolicyService.searchCouponPoliciesByName("NonExistentPolicy"));
    }



}
