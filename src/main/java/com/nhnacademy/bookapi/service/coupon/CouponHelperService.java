package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.member.MemberNotFoundException;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.entity.CouponStatus;
import com.nhnacademy.bookapi.exception.CouponPolicyNotFoundException;
import com.nhnacademy.bookapi.repository.CouponRepository;
import com.nhnacademy.bookapi.repository.CouponPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponHelperService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponService couponService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request) {
        // 1. 쿠폰 정책 조회
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("쿠폰 정책을 찾을 수 없습니다."));

        // 2. 발급 대상 조회
        List<Long> memberIds = couponService.getMemberIdsByRecipientType(request);
        if (memberIds.isEmpty()) {
            throw new MemberNotFoundException("발급 대상 회원이 없습니다.");
        }

        // 3. 쿠폰 생성 및 발급
        List<Coupon> coupons = memberIds.stream()
                .map(memberId -> {
                    Coupon coupon = couponService.createCouponBasedOnTarget(request, policy);
                    coupon.setCouponAssignData(
                            memberId,
                            LocalDate.now(),
                            LocalDate.now().plusDays(policy.getCouponValidTime()),
                            CouponStatus.NOTUSED
                    );
                    return coupon;
                })
                .toList();

        // 4. 배치 저장
        List<Coupon> savedCoupons = couponRepository.saveAll(coupons);

        // 5. 응답 생성
        return savedCoupons.stream()
                .map(savedCoupon -> new CouponAssignResponseDTO(savedCoupon.getId(), "쿠폰 발급 성공"))
                .toList();
    }
}
