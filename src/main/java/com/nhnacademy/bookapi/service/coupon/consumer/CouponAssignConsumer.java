package com.nhnacademy.bookapi.service.coupon.consumer;
import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
import com.nhnacademy.bookapi.dto.coupon.CouponAssignResponseDTO;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponStatus;
import com.nhnacademy.bookapi.exception.CouponAlreadyAssignedException;
import com.nhnacademy.bookapi.exception.CouponNotFoundException;
import com.nhnacademy.bookapi.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CouponAssignConsumer {
    private final CouponRepository couponRepository;

//    @RabbitListener(queues = "nhn24.coupon.queue")
//    @Transactional
//    public CouponAssignResponseDTO handleCouponAssign(CouponAssignRequestDTO request) {
//        // 쿠폰 조회
//        Coupon coupon = couponRepository.findById(request.getCouponId())
//                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
//
//        // 이미 발급된 쿠폰인지 확인
//        if (coupon.getMemberId() != null) {
//            throw new CouponAlreadyAssignedException("Coupon is already assigned");
//        }
//
//        // 쿠폰 발급
//        Integer validTime = coupon.getCouponPolicy().getCouponValidTime();
//        coupon.setMemberId(request.getMemberId());
//        coupon.setCouponIssueDate(LocalDate.now());
//        coupon.setCouponExpiryDate(LocalDate.now().plusDays(validTime));
//        coupon.setCouponStatus(CouponStatus.NOTUSED);
//
//        couponRepository.save(coupon);
//
//        // 응답 메시지 생성
//        return new CouponAssignResponseDTO(coupon);
//    }
}
