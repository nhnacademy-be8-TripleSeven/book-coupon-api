package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.CouponCreateDTO;
import com.nhnacademy.bookapi.dto.coupon.CouponResponseDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.exception.CouponNotFoundException;
import com.nhnacademy.bookapi.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // 일반 쿠폰 생성
    @Override
    public CouponResponseDTO createCoupon(CouponCreateDTO request) {

        Coupon coupon = new Coupon();

        coupon.setName(request.getName());
        coupon.setCouponPolicy(request.getCouponPolicy());

        Coupon savedCoupon = couponRepository.save(coupon);

        return toResponse(savedCoupon);

    }


    // 발급 전 쿠폰 수정
    @Override
    public CouponResponseDTO updateCoupon(long id, CouponCreateDTO request) {

        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new CouponNotFoundException("Coupon Not Found"));
        coupon.setName(request.getName());
        coupon.setCouponPolicy(request.getCouponPolicy());

        Coupon updateCoupon = couponRepository.save(coupon);

        return toResponse(updateCoupon);

    }

    @Override
    public void deleteCoupon(long id) {

    }

    private CouponResponseDTO toResponse(Coupon coupon) {

        CouponResponseDTO response = new CouponResponseDTO();
        response.setId(coupon.getId());
        response.setCouponPolicy(coupon.getCouponPolicy());
        response.setName(coupon.getName());
        response.setCouponIssueDate(coupon.getCouponIssueDate());
        response.setCouponExpiryDate(coupon.getCouponExpiryDate());
        response.setCouponStatus(String.valueOf(coupon.getCouponStatus()));
        response.setCouponUseAt(coupon.getCouponUseAt());

        return response;
    }
}
