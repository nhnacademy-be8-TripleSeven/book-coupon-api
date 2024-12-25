package com.nhnacademy.bookapi.service.couponpolicy;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.exception.CouponMinBiggerThanMaxException;
import com.nhnacademy.bookapi.exception.CouponPolicyNotFoundException;
import com.nhnacademy.bookapi.exception.DiscountRateAndAmountException;
import com.nhnacademy.bookapi.repository.CouponPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponPolicyServiceImpl implements CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 정책 생성
    @Override
    @Transactional
    public CouponPolicyResponseDTO createCouponPolicy(CouponPolicyRequestDTO request) {
        validateCouponPolicyRequest(request);

        CouponPolicy couponPolicy = new CouponPolicy(request.getName(), request.getCouponMinAmount(), request.getCouponMaxAmount(),
                request.getCouponDiscountRate(), request.getCouponDiscountAmount(), request.getCouponValidTime());

        CouponPolicy savedCouponPolicy = couponPolicyRepository.save(couponPolicy);

        return toResponse(savedCouponPolicy);
    }

    // 쿠폰 정책 수정
    @Override
    @Transactional
    public CouponPolicyResponseDTO updateCouponPolicy(Long id, CouponPolicyRequestDTO request) {
        validateCouponPolicyRequest(request);

        CouponPolicy policy = couponPolicyRepository.findById(id)
                .orElseThrow(() -> new CouponPolicyNotFoundException("CouponPolicy not found"));

        policy.setCouponPolicyUpdateData(request.getName(), request.getCouponMinAmount(),request.getCouponMaxAmount(),
                request.getCouponDiscountRate(), request.getCouponDiscountAmount(), request.getCouponValidTime());

        CouponPolicy updatedCouponPolicy = couponPolicyRepository.save(policy);

        return toResponse(updatedCouponPolicy);

    }

    // 쿠폰 정책 삭제
    @Override
    @Transactional
    public void deleteCouponPolicy(Long id) {

        if (!couponPolicyRepository.existsById(id)) {
            throw new CouponPolicyNotFoundException("CouponPolicy not found");
        }

        couponPolicyRepository.deleteById(id);
    }

    // 모든 쿠폰 정책 조회
// 모든 쿠폰 정책 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponPolicyResponseDTO> getAllCouponPolicies() {
        List<CouponPolicy> policies = couponPolicyRepository.findAll();

        if (policies.isEmpty()) {
            throw new CouponPolicyNotFoundException("No coupon policies found.");
        }

        return policies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    // 쿠폰 정책 아이디로 검색
    @Override
    @Transactional(readOnly = true)
    public CouponPolicyResponseDTO getCouponPolicyById(Long id) {
        CouponPolicy policy = couponPolicyRepository.findById(id)
                .orElseThrow(() -> new CouponPolicyNotFoundException("CouponPolicy not found"));

        return toResponse(policy);
    }

    // 쿠폰 정책 이름으로 검색
    @Override
    @Transactional(readOnly = true)
    public CouponPolicyResponseDTO getCouponPolicyByName(String name) {
        CouponPolicy policy = couponPolicyRepository.findByName(name)
                .orElseThrow(() -> new CouponPolicyNotFoundException("CouponPolicy not found"));

        return toResponse(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponPolicyResponseDTO> searchCouponPoliciesByName(String name) {
        List<CouponPolicy> policies = couponPolicyRepository.findByNameContainingIgnoreCase(name);

        if (policies.isEmpty()) {
            throw new CouponPolicyNotFoundException("No coupon policies found matching the query: " + name);
        }

        return policies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 쿠폰 응답 DTO 제작 메소드
    private CouponPolicyResponseDTO toResponse(CouponPolicy policy) {
        CouponPolicyResponseDTO response = new CouponPolicyResponseDTO(policy.getId(), policy.getName(),
                policy.getCouponMinAmount(), policy.getCouponMaxAmount(), policy.getCouponDiscountRate(),
                policy.getCouponDiscountAmount(), policy.getCouponValidTime());
        return response;
    }

    // 정책 등록, 수정 시 예외처리 메소드
    private void validateCouponPolicyRequest(CouponPolicyRequestDTO request) {
        if (request.getCouponMinAmount() > request.getCouponMaxAmount()) {
            throw new CouponMinBiggerThanMaxException("Minimum amount cannot be greater than maximum amount.");
        }

        boolean isDiscountRateZero = request.getCouponDiscountRate() == null || request.getCouponDiscountRate().compareTo(BigDecimal.ZERO) == 0;
        boolean isDiscountAmountZero = request.getCouponDiscountAmount() == null || request.getCouponDiscountAmount() == 0L;

        if (isDiscountRateZero == isDiscountAmountZero) {
            throw new DiscountRateAndAmountException("Either discount rate or discount amount must be zero.");
        }
    }
}
