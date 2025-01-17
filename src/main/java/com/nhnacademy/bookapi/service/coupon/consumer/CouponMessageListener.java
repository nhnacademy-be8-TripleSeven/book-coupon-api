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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;




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

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME, concurrency = "1")
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
            retryOrMoveToDlq(channel, message, messageId);
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

    public void processCouponAssignment(CouponAssignRequestDTO request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found: " + request.getCouponId()));

        if (coupon.getMemberId() != null) {
            log.warn("Coupon is already assigned. Skipping processing: {}", coupon.getId());
            throw new CouponAlreadyAssignedException("Coupon is already assigned: " + coupon.getId());
        }

        coupon.setCouponAssignData(request.getMemberId(), LocalDate.now(),
                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.NOTUSED);
        couponRepository.saveAndFlush(coupon);

        log.info("Coupon successfully assigned: {}", coupon.getId());
    }


    private void retryOrMoveToDlq(Channel channel, Message message, String messageId) {
        try {
            int retryCount = retryStateService.getRetryCount(messageId).incrementAndGet();

            if (retryStateService.isMaxRetriesExceeded(messageId, MAX_RETRY_COUNT)) {
                log.warn("Max retries reached for message ID: {}. Moving to DLQ.", messageId);
                moveToDlq(channel, message);
                retryStateService.resetRetryCount(messageId);
            } else {
                log.info("Retrying message ID: {}. Retry count: {}", messageId, retryCount);
                if (channel.isOpen()) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                } else {
                    log.error("Channel is closed. Cannot retry message ID: {}", messageId);
                }
            }
        } catch (IOException e) {
            log.error("Failed to retry or move message to DLQ: {}", e.getMessage(), e);
        }
    }


    private void moveToDlq(Channel channel, Message message) {
        try {
            if (channel.isOpen()) {
                log.info("Moving message to DLQ: {}", new String(message.getBody()));
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                log.info("Message moved to DLQ successfully: {}", message.getMessageProperties().getMessageId());
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
