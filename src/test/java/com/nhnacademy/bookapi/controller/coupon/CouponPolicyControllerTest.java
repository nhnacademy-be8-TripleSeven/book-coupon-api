package com.nhnacademy.bookapi.controller.coupon;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CouponPolicyControllerTest {

    @InjectMocks
    private CouponPolicyController couponPolicyController;

    @Mock
    private CouponPolicyService couponPolicyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCouponPolicy() {
        // Given
        CouponPolicyRequestDTO requestDTO = new CouponPolicyRequestDTO("New Policy", 100L, 500L, null, null, 30);
        CouponPolicyResponseDTO responseDTO = new CouponPolicyResponseDTO(1L, "New Policy", 100L, 500L, null, null, 30);

        when(couponPolicyService.createCouponPolicy(any(CouponPolicyRequestDTO.class)))
                .thenReturn(responseDTO);

        // When
        ResponseEntity<CouponPolicyResponseDTO> response = couponPolicyController.createCouponPolicy(requestDTO);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testUpdateCouponPolicy() {
        // Given
        Long id = 1L;
        CouponPolicyRequestDTO requestDTO = new CouponPolicyRequestDTO("Updated Policy", 150L, 700L, null, null, 45);
        CouponPolicyResponseDTO responseDTO = new CouponPolicyResponseDTO(1L, "Updated Policy", 150L, 700L, null, null, 45);

        when(couponPolicyService.updateCouponPolicy(eq(id), any(CouponPolicyRequestDTO.class)))
                .thenReturn(responseDTO);

        // When
        ResponseEntity<CouponPolicyResponseDTO> response = couponPolicyController.updateCouponPolicy(id, requestDTO);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testDeleteCouponPolicy() {
        // Given
        Long id = 1L;
        doNothing().when(couponPolicyService).deleteCouponPolicy(id);

        // When
        ResponseEntity<Void> response = couponPolicyController.deleteCouponPolicy(id);

        // Then
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testGetAllCouponPolicies() {
        // Given
        CouponPolicyResponseDTO responseDTO = new CouponPolicyResponseDTO(1L, "Test Policy", 100L, 500L, null, null, 30);
        List<CouponPolicyResponseDTO> responseList = List.of(responseDTO);

        when(couponPolicyService.getAllCouponPolicies()).thenReturn(responseList);

        // When
        ResponseEntity<List<CouponPolicyResponseDTO>> response = couponPolicyController.getAllCouponPolicies();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseList, response.getBody());
    }

    @Test
    void testGetCouponPolicyById() {
        // Given
        Long id = 1L;
        CouponPolicyResponseDTO responseDTO = new CouponPolicyResponseDTO(1L, "Test Policy", 100L, 500L, null, null, 30);

        when(couponPolicyService.getCouponPolicyById(id)).thenReturn(responseDTO);

        // When
        ResponseEntity<CouponPolicyResponseDTO> response = couponPolicyController.getCouponPolicyById(id);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetCouponPolicyByName() {
        // Given
        String name = "Test Policy";
        CouponPolicyResponseDTO responseDTO = new CouponPolicyResponseDTO(1L, "Test Policy", 100L, 500L, null, null, 30);

        when(couponPolicyService.getCouponPolicyByName(name)).thenReturn(responseDTO);

        // When
        ResponseEntity<CouponPolicyResponseDTO> response = couponPolicyController.getCouponPolicyByName(name);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testSearchCouponPoliciesByName() {
        // Given
        String query = "Policy";
        CouponPolicyResponseDTO responseDTO = new CouponPolicyResponseDTO(1L, "Policy 1", 100L, 500L, null, null, 30);
        List<CouponPolicyResponseDTO> responseList = List.of(responseDTO);

        when(couponPolicyService.searchCouponPoliciesByName(query)).thenReturn(responseList);

        // When
        ResponseEntity<List<CouponPolicyResponseDTO>> response = couponPolicyController.searchCouponPoliciesByName(query);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseList, response.getBody());
    }
}
