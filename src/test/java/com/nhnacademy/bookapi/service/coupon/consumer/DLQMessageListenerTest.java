package com.nhnacademy.bookapi.service.coupon.consumer;

import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DLQMessageListenerTest {
    @InjectMocks
    private DLQMessageListener dlqMessageListener;

    @Mock
    private RetryStateService retryStateService;

    @Mock
    private CouponMessageListener couponMessageListener;

    @Mock
    private Channel channel;

    private static final String MESSAGE_ID = "test-message-id";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(retryStateService.getRetryCount(MESSAGE_ID)).thenReturn(new AtomicInteger(0));
    }

    private Message createTestMessage(String messageContent) {
        MessageProperties properties = new MessageProperties();
        properties.setMessageId(MESSAGE_ID);
        return new Message(messageContent.getBytes(), properties);
    }

    @Test
    void testHandleDlqMessages_SuccessfulReprocess() throws IOException {
        // Given
        String messageContent = "{\"couponId\":1,\"memberId\":123}";
        Message message = createTestMessage(messageContent);
        when(retryStateService.getRetryCount(MESSAGE_ID)).thenReturn(new AtomicInteger(0));

        // When
        dlqMessageListener.handleDlqMessages(message, channel);

        // Then
        verify(couponMessageListener, times(1)).processCouponAssignment(any(CouponAssignRequestDTO.class));
        verify(channel, times(1)).basicAck(anyLong(), eq(false));
        verify(retryStateService, times(1)).resetRetryCount(MESSAGE_ID);
    }

    @Test
    void testHandleDlqMessages_MaxRetriesExceeded() throws IOException {
        // Given
        String messageContent = "{\"couponId\":1,\"memberId\":123}";
        Message message = createTestMessage(messageContent);
        when(retryStateService.getRetryCount(MESSAGE_ID)).thenReturn(new AtomicInteger(4)); // MAX_RETRY_COUNT 초과

        // When
        dlqMessageListener.handleDlqMessages(message, channel);

        // Then
        verify(couponMessageListener, never()).processCouponAssignment(any(CouponAssignRequestDTO.class));
        verify(channel, times(1)).basicReject(anyLong(), eq(false));
        verify(retryStateService, times(1)).resetRetryCount(MESSAGE_ID);
    }

    @Test
    void testHandleDlqMessages_ParsingFailure() throws IOException {
        // Given
        String invalidMessageContent = "invalid-json";
        Message message = createTestMessage(invalidMessageContent);

        // When
        dlqMessageListener.handleDlqMessages(message, channel);

        // Then
        verify(couponMessageListener, never()).processCouponAssignment(any(CouponAssignRequestDTO.class));
        verify(channel, times(1)).basicReject(anyLong(), eq(false));
        verify(retryStateService, never()).resetRetryCount(MESSAGE_ID);
    }

    @Test
    void testHandleDlqMessages_ChannelIOExceptionOnReject() throws IOException {
        // Given
        String invalidMessageContent = "invalid-json";
        Message message = createTestMessage(invalidMessageContent);
        doThrow(new IOException("Test IO Exception")).when(channel).basicReject(anyLong(), eq(false));

        // When
        dlqMessageListener.handleDlqMessages(message, channel);

        // Then
        verify(channel, times(1)).basicReject(anyLong(), eq(false));
    }

    @Test
    void testHandleDlqMessages_ExceptionDuringProcessing() throws IOException {
        // Given
        String messageContent = "{\"couponId\":1,\"memberId\":123}";
        Message message = createTestMessage(messageContent);
        doThrow(new RuntimeException("Test Runtime Exception"))
                .when(couponMessageListener).processCouponAssignment(any(CouponAssignRequestDTO.class));

        // When
        dlqMessageListener.handleDlqMessages(message, channel);

        // Then
        verify(couponMessageListener, times(1)).processCouponAssignment(any(CouponAssignRequestDTO.class));
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
        verify(retryStateService, never()).resetRetryCount(MESSAGE_ID);
    }

    @Test
    void testHandleDlqMessages_ChannelIOExceptionOnAck() throws IOException {
        // Given
        String messageContent = "{\"couponId\":1,\"memberId\":123}";
        Message message = createTestMessage(messageContent);
        doThrow(new IOException("Test IO Exception")).when(channel).basicAck(anyLong(), eq(false));

        // When
        dlqMessageListener.handleDlqMessages(message, channel);

        // Then
        verify(couponMessageListener, times(1)).processCouponAssignment(any(CouponAssignRequestDTO.class));
        verify(channel, times(1)).basicAck(anyLong(), eq(false));
    }
}
