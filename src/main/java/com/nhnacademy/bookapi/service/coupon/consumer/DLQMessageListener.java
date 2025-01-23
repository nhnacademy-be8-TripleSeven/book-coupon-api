package com.nhnacademy.bookapi.service.coupon.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
import com.nhnacademy.bookapi.exception.MessageParserException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DLQMessageListener {
    private final RetryStateService retryStateService;
    private final CouponMessageListener couponMessageListener;
    private static final int MAX_RETRY_COUNT = 3;

    public DLQMessageListener(RetryStateService retryStateService, CouponMessageListener couponMessageListener) {
        this.retryStateService = retryStateService;
        this.couponMessageListener = couponMessageListener;
    }

    @RabbitListener(queues = RabbitConfig.DLQ_NAME)
    public void handleDlqMessages(Message message, Channel channel) {
        String messageId = message.getMessageProperties().getMessageId();
        String messageContent = new String(message.getBody());

        log.error("DLQ Message received: {}", messageContent);

        try {
            int retryCount = retryStateService.getRetryCount(messageId).incrementAndGet();

            if (retryCount > MAX_RETRY_COUNT) {
                log.error("Max retries exceeded for DLQ message. Skipping further processing. Message ID: {}", messageId);
                retryStateService.resetRetryCount(messageId);
                // 메시지 폐기
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            log.info("Retrying DLQ message processing. Attempt: {}", retryCount);

            // 기존 쿠폰 처리 로직 호출
            CouponAssignRequestDTO request = parseMessageContent(messageContent);
            couponMessageListener.processCouponAssignment(request);

            // 성공 시 ack 처리
            log.info("DLQ message successfully reprocessed: {}", messageId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            retryStateService.resetRetryCount(messageId);
        } catch (MessageParserException e) {
            log.error("Message parsing failed for message ID: {}. Skipping further processing.", messageId, e);
            // 메시지 폐기
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception ex) {
                log.error("Failed to reject message in DLQ: {}", ex.getMessage(), ex);
            }
        } catch (Exception e) {
            log.error("Failed to reprocess DLQ message. Message ID: {}", messageId, e);
        }
    }

    private CouponAssignRequestDTO parseMessageContent(String messageContent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(messageContent, CouponAssignRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new MessageParserException("Failed to parse message content: " + messageContent);
        }
    }
}
