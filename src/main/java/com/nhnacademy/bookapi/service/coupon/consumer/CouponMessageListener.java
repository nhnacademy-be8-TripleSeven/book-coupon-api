//package com.nhnacademy.bookapi.service.coupon.consumer;
//
//import com.nhnacademy.bookapi.config.RabbitConfig;
//import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
//import com.nhnacademy.bookapi.entity.Coupon;
//import com.nhnacademy.bookapi.entity.CouponStatus;
//import com.nhnacademy.bookapi.exception.CouponAlreadyAssignedException;
//import com.nhnacademy.bookapi.exception.CouponNotFoundException;
//import com.nhnacademy.bookapi.repository.CouponRepository;
//import com.rabbitmq.client.Channel;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.time.LocalDate;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CouponMessageListener {
//    private final CouponRepository couponRepository;
//
//    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
//    @Transactional
//    public void handleCouponAssignRequest(CouponAssignRequestDTO request, Message message, Channel channel) {
//        log.info("Received coupon assign request: {}", request);
//
//        try {
//            // 쿠폰 조회
//            Coupon coupon = couponRepository.findById(request.getCouponId())
//                    .orElseThrow(() -> new CouponNotFoundException("Coupon not found: " + request.getCouponId()));
//
//            // 이미 할당된 쿠폰인지 확인
//            if (coupon.getMemberId() != null) {
//                log.warn("Coupon already assigned: {}", coupon);
//                throw new CouponAlreadyAssignedException("Coupon is already assigned: " + coupon.getId());
//            }
//
//            // 쿠폰 정보 업데이트
//            coupon.setCouponAssignData(request.getMemberId(), LocalDate.now(),
//                    LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()),CouponStatus.NOTUSED);
//
//            couponRepository.saveAndFlush(coupon); // DB 저장
//            log.info("Coupon successfully assigned: {}", coupon);
//
//            // 성공적으로 처리된 경우 메시지 확인(Ack)
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        } catch (CouponNotFoundException | CouponAlreadyAssignedException e) {
//            log.error("Business exception: {}", e.getMessage());
//            try {
//                // 처리 불가능한 메시지를 DLQ로 이동
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//            } catch (IOException ioException) {
//                log.error("Failed to reject the message: {}", ioException.getMessage(), ioException);
//            }
//        } catch (Exception e) {
//            log.error("Error processing message: {}", e.getMessage(), e);
//            try {
//                // 기타 에러 발생 시 재시도 처리
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//            } catch (IOException ioException) {
//                log.error("Failed to nack the message: {}", ioException.getMessage(), ioException);
//            }
//        }
//    }
//}





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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CouponMessageListener {
//    private final CouponRepository couponRepository;
//    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();
//
//    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
//    @Transactional
//    public void handleCouponAssignRequest(CouponAssignRequestDTO request, Message message, Channel channel) {
//        String messageId = getMessageId(message);
//
//        try {
//            log.info("Received coupon assign request: {}", request);
//
//            // 쿠폰 할당 처리
//            processCouponAssignment(request);
//
//            // 메시지 처리가 성공하면 Ack를 호출하여 RabbitMQ에 확인 신호를 보냄
//            if (channel.isOpen()) {
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//                retryCounts.remove(messageId);
//                log.info("Message acknowledged: {}", messageId);
//            }
//
//        } catch (CouponAlreadyAssignedException | CouponNotFoundException e) {
//            // 비즈니스 예외가 발생하면 DLQ로 이동하거나 재처리를 시도
//            log.warn("Business exception: {}", e.getMessage());
//            retryOrMoveToDlq(channel, message, messageId);
//        } catch (Exception e) {
//            // 예상치 못한 예외가 발생했을 때 DLQ로 이동하거나 재처리를 시도
//            log.warn("Unexpected error processing message: {}", e.getMessage(), e);
//            retryOrMoveToDlq(channel, message, messageId);
//        }
//    }
//
//    @Transactional
//    protected void processCouponAssignment(CouponAssignRequestDTO request) {
//        // 쿠폰 ID로 쿠폰 조회
//        Coupon coupon = couponRepository.findById(request.getCouponId())
//                .orElseThrow(() -> new CouponNotFoundException("Coupon not found: " + request.getCouponId()));
//
//        // 쿠폰이 이미 할당된 상태인지 확인
//        if (coupon.getMemberId() != null) {
//            throw new CouponAlreadyAssignedException("Coupon is already assigned: " + coupon.getId());
//        }
//
//        // 쿠폰 데이터 업데이트 (회원 ID, 발급일, 만료일 등)
//        coupon.setCouponAssignData(request.getMemberId(), LocalDate.now(),
//                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.NOTUSED);
//
//        // 업데이트된 쿠폰 데이터를 DB에 저장
//        couponRepository.saveAndFlush(coupon);
//        log.info("Coupon successfully assigned: {}", coupon);
//    }
//
////    private void retryOrMoveToDlq(Channel channel, Message message, String messageId) {
////        try {
////            // 현재 메시지의 재시도 횟수 확인
////            int retryCount = retryCounts.getOrDefault(messageId, 0);
////
////            if (retryCount >= 2) {
////                // 최대 재시도 횟수를 초과하면 메시지를 DLQ로 이동
////                log.info("Max retries reached for message: {}. Moving to DLQ.", messageId);
////                moveToDlq(channel, message);
////                retryCounts.remove(messageId);
////            } else {
////                // 재시도 횟수를 증가시키고 메시지를 재처리하도록 Nack 호출
////                retryCounts.put(messageId, retryCount + 1);
////                log.info("Retrying message: {}. Retry count: {}", messageId, retryCount + 1);
////                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
////            }
////        } catch (IOException e) {
////            // Nack 또는 메시지 이동 실패 시 로그 기록
////            log.info("Failed to nack/requeue message: {}", e.getMessage(), e);
////        }
////    }
//
//    private void retryOrMoveToDlq(Channel channel, Message message, String messageId) {
//        try {
//            if (channel.isOpen()) {
//                int retryCount = retryCounts.getOrDefault(messageId, 0);
//
//                if (retryCount >= 2) {
//                    log.info("Max retries reached for message: {}. Moving to DLQ.", messageId);
//                    moveToDlq(channel, message);
//                    retryCounts.remove(messageId);
//                } else {
//                    retryCounts.put(messageId, retryCount + 1);
//                    log.info("Retrying message: {}. Retry count: {}", messageId, retryCount + 1);
//                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//                }
//            } else {
//                log.info("Channel is closed; cannot ack/nack. Skipping message processing.");
//            }
//        } catch (IOException e) {
//            log.info("Failed to nack/requeue message: {}", e.getMessage(), e);
//        }
//    }
//
//
//    private void moveToDlq(Channel channel, Message message) {
//        try {
//            if (channel.isOpen()) {
//                // 메시지를 DLQ로 이동
//                log.info("Moving message to DLQ: MessageId={}, Content={}",
//                        message.getMessageProperties().getMessageId(), new String(message.getBody()));
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//            } else {
//                log.info("Channel is closed; cannot move message to DLQ");
//            }
//        } catch (IOException e) {
//            // DLQ 이동 실패 시 로그 기록
//            log.info("Failed to move message to DLQ: {}", e.getMessage(), e);
//        }
//    }
//
//    private String getMessageId(Message message) {
//        // RabbitMQ 메시지 ID가 없으면 UUID로 생성
//        String messageId = message.getMessageProperties().getMessageId();
//        if (messageId == null || messageId.isEmpty()) {
//            messageId = UUID.randomUUID().toString();
//            message.getMessageProperties().setMessageId(messageId);
//        }
//        return messageId;
//    }
//}



@Slf4j
@Service
@RequiredArgsConstructor
public class CouponMessageListener {
    private final CouponRepository couponRepository;
    private final RetryStateService retryStateService;
    private static final int MAX_RETRY_COUNT = 3;
    private final Set<String> processingMessages = ConcurrentHashMap.newKeySet();

    private boolean isAlreadyProcessing(String messageId) {
        return !processingMessages.add(messageId);
    }

    private void clearProcessing(String messageId) {
        processingMessages.remove(messageId);
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @Transactional
    public void handleCouponAssignRequest(CouponAssignRequestDTO request, Message message, Channel channel) {
        String messageId = getMessageId(message);

        if (isAlreadyProcessing(messageId)) {
            log.warn("Duplicate message detected. Skipping processing for message ID: {}", messageId);
            return;
        }

        try {
            log.info("Processing message ID: {}", messageId);
            processCouponAssignment(request);
            acknowledgeMessage(channel, message.getMessageProperties().getDeliveryTag(), messageId);
        } catch (CouponAlreadyAssignedException e) {
            log.error("Coupon is already assigned: {}", e.getMessage());
            moveToDlq(channel, message);
        } catch (CouponNotFoundException e) {
            log.error("Coupon not found: {}", e.getMessage());
            retryOrMoveToDlq(channel, message, messageId);
        } catch (Exception e) {
            log.error("Unexpected error processing message ID: {}", messageId, e);
            retryOrMoveToDlq(channel, message, messageId);
        } finally {
            clearProcessing(messageId);
        }
    }

    private void processCouponAssignment(CouponAssignRequestDTO request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found: " + request.getCouponId()));

        if (coupon.getMemberId() != null) {
            log.warn("Coupon is already assigned. Skipping processing: {}", coupon.getId());
            throw new CouponAlreadyAssignedException("Coupon is already assigned: " + coupon.getId());
        }

        coupon.setCouponAssignData(request.getMemberId(), LocalDate.now(),
                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.NOTUSED);
        couponRepository.saveAndFlush(coupon);

        log.info("Coupon successfully assigned: {}", coupon);
    }


    private void retryOrMoveToDlq(Channel channel, Message message, String messageId) {
        try {
            int retryCount = retryStateService.getRetryCount(messageId).incrementAndGet();
            if (retryStateService.isMaxRetriesExceeded(messageId, MAX_RETRY_COUNT) || !channel.isOpen()) {
                log.warn("Max retries reached or channel closed for message: {}. Moving to DLQ.", messageId);
                moveToDlq(channel, message);
                retryStateService.resetRetryCount(messageId);
            } else {
                log.info("Retrying message: {}. Retry count: {}", messageId, retryCount);
                if (channel.isOpen()) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            }
        } catch (IOException e) {
            log.error("Error handling retry logic for message: {}", messageId, e);
        }
    }

    private void moveToDlq(Channel channel, Message message) {
        try {
            if (channel.isOpen()) {
                log.info("Moving message to DLQ: {}", new String(message.getBody()));
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("Channel is closed. Cannot move message to DLQ.");
            }
        } catch (IOException e) {
            log.error("Failed to move message to DLQ: {}", e.getMessage(), e);
        }
    }

    private void acknowledgeMessage(Channel channel, long deliveryTag, String messageId) {
        try {
            if (channel.isOpen()) {
                channel.basicAck(deliveryTag, false);
                retryStateService.resetRetryCount(messageId);
                log.info("Message acknowledged: {}", messageId);
            } else {
                log.warn("Channel is closed. Cannot acknowledge message: {}", messageId);
            }
        } catch (IOException e) {
            log.error("Failed to acknowledge message: {}", e.getMessage(), e);
        }
    }

    private String getMessageId(Message message) {
        String messageId = message.getMessageProperties().getMessageId();
        if (messageId == null || messageId.isEmpty()) {
            messageId = UUID.randomUUID().toString();
            message.getMessageProperties().setMessageId(messageId);
        }
        log.info("Message ID: {}", messageId);
        return messageId;
    }
}
