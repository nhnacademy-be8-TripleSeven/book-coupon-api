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
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponMessageListener {
    private final CouponRepository couponRepository;
    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @Transactional
    public void handleCouponAssignRequest(CouponAssignRequestDTO request, Message message, Channel channel) {
        log.info("Received coupon assign request: {}", request);

        String messageId = getMessageId(message);
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
                    LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.NOTUSED);

            couponRepository.saveAndFlush(coupon); // DB 저장
            log.info("Coupon successfully assigned: {}", coupon);

            // 성공적으로 처리된 경우 메시지 확인(Ack)
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            retryCounts.remove(messageId);

        } catch (CouponNotFoundException | CouponAlreadyAssignedException e) {
            log.error("Business exception: {}", e.getMessage());
            retryOrMoveToDlq(channel, message, messageId);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            retryOrMoveToDlq(channel, message, messageId);
        }
    }

    private void retryOrMoveToDlq(Channel channel, Message message, String messageId) {
        try {
            int retryCount = retryCounts.getOrDefault(messageId, 0);

            if (retryCount >= 2) {
                log.info("Max retries reached for message: {}. Moving to DLQ.", messageId);
                moveToDlq(channel, message);
                retryCounts.remove(messageId); // 성공적으로 처리 후 제거
            } else {
                retryCounts.put(messageId, retryCount + 1);
                log.info("Retrying message: {}. Retry count: {}", messageId, retryCount + 1);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true); // 재처리 요청
            }
        } catch (IOException ioException) {
            log.error("Failed to nack the message: {}", ioException.getMessage(), ioException);
        }
    }

    private void moveToDlq(Channel channel, Message message) {
        try {
            String messageContent = new String(message.getBody()); // 메시지 본문을 문자열로 변환
            log.info("Moving message to DLQ: Content={}, MessageId={}", messageContent, message.getMessageProperties().getMessageId());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // requeue=false
        } catch (IOException ioException) {
            log.error("Failed to move message to DLQ: {}", ioException.getMessage(), ioException);
        }
    }


    private String getMessageId(Message message) {
        String messageId = message.getMessageProperties().getMessageId();
        if (messageId == null || messageId.isEmpty()) {
            messageId = "msg-" + new String(message.getBody()).hashCode(); // 메시지 본문 기반 ID 생성
            message.getMessageProperties().setMessageId(messageId);
        }
        return messageId;
    }

}