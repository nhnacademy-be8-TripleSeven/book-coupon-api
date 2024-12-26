package com.nhnacademy.bookapi.service.coupon.consumer;

import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponStatus;
import com.nhnacademy.bookapi.exception.CouponAlreadyAssignedException;
import com.nhnacademy.bookapi.exception.CouponNotFoundException;
import com.nhnacademy.bookapi.repository.CouponRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponMessageListener {
    private final CouponRepository couponRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @Transactional
    public void handleCouponAssignRequest(CouponAssignRequestDTO request, Message message, Channel channel) {
        log.info("Received coupon assign request: {}", request);

        try {
            // 쿠폰 조회
            Coupon coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> new CouponNotFoundException("Coupon not found: " + request.getCouponId()));

            // 이미 할당된 쿠폰인지 확인
            if (coupon.getMemberId() != null) {
                log.warn("Coupon already assigned: {}", coupon);
                throw new CouponAlreadyAssignedException("Coupon is already assigned: " + coupon.getId());
            }

            // 쿠폰 정보 업데이트
            coupon.setCouponAssignData(request.getMemberId(), LocalDate.now(),
                    LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()),CouponStatus.NOTUSED);

            couponRepository.saveAndFlush(coupon); // DB 저장
            log.info("Coupon successfully assigned: {}", coupon);

            // 성공적으로 처리된 경우 메시지 확인(Ack)
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (CouponNotFoundException | CouponAlreadyAssignedException e) {
            log.error("Business exception: {}", e.getMessage());
            try {
                // 처리 불가능한 메시지를 DLQ로 이동
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException ioException) {
                log.error("Failed to reject the message: {}", ioException.getMessage(), ioException);
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            try {
                // 기타 에러 발생 시 재시도 처리
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                log.error("Failed to nack the message: {}", ioException.getMessage(), ioException);
            }
        }
    }
}
