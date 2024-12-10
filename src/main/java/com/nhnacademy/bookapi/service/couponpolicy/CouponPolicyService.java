package com.nhnacademy.bookapi.service.couponpolicy;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.entity.CouponPolicy;

import java.util.List;

public interface CouponPolicyService {
    CouponPolicyResponseDTO createCouponPolicy(CouponPolicyRequestDTO request);

    CouponPolicyResponseDTO updateCouponPolicy(Long id, CouponPolicyRequestDTO request);

    void deleteCouponPolicy(Long id);

    List<CouponPolicyResponseDTO> getAllCouponPolicies();

    CouponPolicyResponseDTO getCouponPolicyById(Long id);

    CouponPolicyResponseDTO getCouponPolicyByName(String name);
}
