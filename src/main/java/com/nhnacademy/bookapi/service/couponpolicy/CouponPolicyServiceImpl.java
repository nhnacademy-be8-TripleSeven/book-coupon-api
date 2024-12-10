package com.nhnacademy.bookapi.service.couponpolicy;

import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyRequestDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.exception.CouponPolicyNotFoundException;
import com.nhnacademy.bookapi.repository.CouponPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponPolicyServiceImpl implements CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    public CouponPolicyServiceImpl(CouponPolicyRepository repoisitory) {
        this.couponPolicyRepository = repoisitory;
    }

    // 쿠폰 정책 생성
    @Override
    public CouponPolicyResponseDTO createCouponPolicy(CouponPolicyRequestDTO request) {
        CouponPolicy couponPolicy = new CouponPolicy();
        couponPolicy.setName(request.getName());
        couponPolicy.setCouponDiscountAmount(request.getCouponDiscountAmount());
        couponPolicy.setCouponDiscountRate(request.getCouponDiscountRate());
        couponPolicy.setCouponMaxAmount(request.getCouponMaxAmount());
        couponPolicy.setCouponMinAmount(request.getCouponMinAmount());
        couponPolicy.setCouponValidTime(request.getCouponValidTime());

        CouponPolicy savedCouponPolicy = couponPolicyRepository.save(couponPolicy);

        return toResponse(savedCouponPolicy);
    }

    // 쿠폰 정책 수정
    @Override
    public CouponPolicyResponseDTO updateCouponPolicy(Long id, CouponPolicyRequestDTO request) {

        CouponPolicy policy = couponPolicyRepository.findById(id)
                .orElseThrow(() -> new CouponPolicyNotFoundException("CouponPolicy not found"));

        policy.setName(request.getName());
        policy.setCouponDiscountAmount(request.getCouponDiscountAmount());
        policy.setCouponDiscountRate(request.getCouponDiscountRate());
        policy.setCouponMaxAmount(request.getCouponMaxAmount());
        policy.setCouponMinAmount(request.getCouponMinAmount());
        policy.setCouponValidTime(request.getCouponValidTime());

        CouponPolicy updatedCouponPolicy = couponPolicyRepository.save(policy);

        return toResponse(updatedCouponPolicy);

    }

    // 쿠폰 정책 삭제
    @Override
    public void deleteCouponPolicy(Long id) {

        if (!couponPolicyRepository.existsById(id)) {
            throw new CouponPolicyNotFoundException("CouponPolicy not found");
        }

        couponPolicyRepository.deleteById(id);
    }

    Coupon coupon = new Coupon();
    // 모든 쿠폰 정책 조회
    @Override
    public List<CouponPolicyResponseDTO> getAllCouponPolicies() {
        return couponPolicyRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // 쿠폰 정책 아이디로 검색
    @Override
    public CouponPolicyResponseDTO getCouponPolicyById(Long id) {
        CouponPolicy policy = couponPolicyRepository.findById(id)
                .orElseThrow(() -> new CouponPolicyNotFoundException("CouponPolicy not found"));

        return toResponse(policy);
    }

    // 쿠폰 정책 이름으로 검색
    @Override
    public CouponPolicyResponseDTO getCouponPolicyByName(String name) {
        CouponPolicy policy = couponPolicyRepository.findByName(name)
                .orElseThrow(() -> new CouponPolicyNotFoundException("CouponPolicy not found"));

        return toResponse(policy);
    }

    // 쿠폰 응답 DTO 제작 메소드
    private CouponPolicyResponseDTO toResponse(CouponPolicy policy) {
        CouponPolicyResponseDTO response = new CouponPolicyResponseDTO();
        response.setId(policy.getId());
        response.setName(policy.getName());
        response.setCouponMinAmount(policy.getCouponMinAmount());
        response.setCouponDiscountRate(policy.getCouponDiscountRate());
        response.setCouponDiscountAmount(policy.getCouponDiscountAmount());
        response.setCouponMaxAmount(policy.getCouponMaxAmount());
        response.setCouponValidTime(policy.getCouponValidTime());
        return response;
    }
}
