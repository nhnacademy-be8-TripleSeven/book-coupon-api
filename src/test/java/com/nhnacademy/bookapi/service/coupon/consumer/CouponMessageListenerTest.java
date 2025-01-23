package com.nhnacademy.bookapi.service.coupon.consumer;

import com.nhnacademy.bookapi.dto.coupon.CouponAssignRequestDTO;
import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.CouponPolicy;
import com.nhnacademy.bookapi.entity.CouponStatus;
import com.nhnacademy.bookapi.repository.CouponRepository;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CouponMessageListenerTest {
    @InjectMocks
    private CouponMessageListener couponMessageListener;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private RetryStateService retryStateService;

    @Mock
    private Channel channel;

    private static final int MAX_RETRY_COUNT = 3;

    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CouponPolicy policy = CouponPolicy.builder()
                .name("Test Policy")
                .couponMinAmount(0L)
                .couponMaxAmount(100L)
                .couponDiscountRate(BigDecimal.valueOf(0.5))
                .couponDiscountAmount(null)
                .couponValidTime(7)
                .build();

        testCoupon = Coupon.builder()
                .id(1L)
                .name("Test Coupon")
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .build();

        when(retryStateService.getRetryCount(anyString())).thenReturn(new AtomicInteger(0));
        when(retryStateService.isMaxRetriesExceeded(anyString(), anyInt())).thenReturn(false);
    }

    @Test
    void testHandleCouponAssignRequest_Success() throws IOException {
        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 123L);
        Message message = createTestMessage("success-message-id");

        when(couponRepository.findById(1L)).thenReturn(Optional.of(testCoupon));
        when(channel.isOpen()).thenReturn(true);

        couponMessageListener.handleCouponAssignRequest(request, message, channel);

        verify(couponRepository, times(1)).saveAndFlush(any(Coupon.class));
        verify(channel, times(1)).basicAck(anyLong(), eq(false));
    }

    @Test
    void testMessageIdAssignmentWhenNull() throws IOException {
        // Given
        Message message = createTestMessage(null);
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));
        when(channel.isOpen()).thenReturn(true);

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        assertNotNull(message.getMessageProperties().getMessageId(), "Message ID should be assigned a UUID.");
        verify(retryStateService, never()).getRetryCount(anyString());
        verify(couponRepository, times(1)).findById(anyLong());
        verify(channel, times(1)).basicAck(anyLong(), eq(false));
    }


    @Test
    void testHandleCouponAssignRequest_DuplicateProcessing() {
        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 123L);
        Message message = createTestMessage("duplicate-message-id");

        couponMessageListener.handleCouponAssignRequest(request, message, channel);
        couponMessageListener.handleCouponAssignRequest(request, message, channel);

        verify(couponRepository, never()).saveAndFlush(any(Coupon.class));
    }

    @Test
    void testHandleCouponAssignRequest_DuplicateProcessing_Integration() throws IOException {
        // Given
        String messageId = "duplicate-message-id";
        Message message = createTestMessage(messageId);

        // 이미 처리 중인 메시지로 설정
        couponMessageListener.isAlreadyProcessing(messageId);

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        verify(couponRepository, never()).saveAndFlush(any(Coupon.class)); // 저장 로직 호출 안됨
        verify(channel, never()).basicAck(anyLong(), anyBoolean()); // Ack 호출 안됨
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean()); // Nack 호출 안됨
    }


    @Test
    void testHandleCouponAssignRequest_CouponNotFoundException() throws IOException {
        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 123L);
        Message message = createTestMessage("notfound-message-id");

        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
        when(retryStateService.getRetryCount("notfound-message-id")).thenReturn(new AtomicInteger(0));
        when(channel.isOpen()).thenReturn(true);

        couponMessageListener.handleCouponAssignRequest(request, message, channel);

        verify(channel, times(1)).basicNack(anyLong(), eq(false), eq(true));
    }

    @Test
    void testHandleCouponAssignRequest_CouponAlreadyAssignedException() throws IOException {
        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 123L);
        Message message = createTestMessage("assigned-message-id");
        testCoupon = testCoupon.toBuilder().memberId(999L).build();

        when(couponRepository.findById(1L)).thenReturn(Optional.of(testCoupon));
        when(retryStateService.getRetryCount("assigned-message-id")).thenReturn(new AtomicInteger(0));
        when(channel.isOpen()).thenReturn(true);

        couponMessageListener.handleCouponAssignRequest(request, message, channel);

        verify(channel, times(1)).basicNack(anyLong(), eq(false), eq(true));
    }

    @Test
    void testHandleCouponAssignRequest_UnexpectedException() throws IOException {
        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 123L);
        Message message = createTestMessage("unexpected-message-id");

        when(couponRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected Error"));
        when(retryStateService.getRetryCount("unexpected-message-id")).thenReturn(new AtomicInteger(0));
        when(channel.isOpen()).thenReturn(true);

        couponMessageListener.handleCouponAssignRequest(request, message, channel);

        verify(channel, times(1)).basicNack(anyLong(), eq(false), eq(true));
    }

    @Test
    void testRetryOrMoveToDlq_MaxRetriesExceeded() throws IOException {
        // Arrange
        Message message = createTestMessage("retry-message-id");

        when(retryStateService.getRetryCount("retry-message-id")).thenReturn(new AtomicInteger(3));
        when(retryStateService.isMaxRetriesExceeded("retry-message-id", 3)).thenReturn(true);
        when(channel.isOpen()).thenReturn(true);

        // Act
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Assert
        verify(channel, times(1)).basicReject(anyLong(), eq(false));
        verify(retryStateService, times(1)).resetRetryCount("retry-message-id");
    }
    @Test
    void testRetryOrMoveToDlq_MaxRetriesExceededWithIOException() throws IOException {
        // Given
        Message message = createTestMessage("retry-max-exceeded-ioexception");
        when(retryStateService.getRetryCount("retry-max-exceeded-ioexception")).thenReturn(new AtomicInteger(3));
        when(retryStateService.isMaxRetriesExceeded("retry-max-exceeded-ioexception", MAX_RETRY_COUNT)).thenReturn(true);
        when(channel.isOpen()).thenReturn(true);
        doThrow(new IOException("Test IO Exception")).when(channel).basicReject(anyLong(), eq(false));

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        verify(channel, times(1)).basicReject(anyLong(), eq(false));
        verify(retryStateService, times(1)).resetRetryCount("retry-max-exceeded-ioexception");
    }

    @Test
    void testRetryOrMoveToDlq_NotYetMaxRetriesWithIOException() throws IOException {
        // Given
        Message message = createTestMessage("retry-not-max-ioexception");
        when(retryStateService.getRetryCount("retry-not-max-ioexception")).thenReturn(new AtomicInteger(2));
        when(retryStateService.isMaxRetriesExceeded("retry-not-max-ioexception", MAX_RETRY_COUNT)).thenReturn(false);
        when(channel.isOpen()).thenReturn(true);
        doThrow(new IOException("Test IO Exception")).when(channel).basicNack(anyLong(), eq(false), eq(true));

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        verify(channel, times(1)).basicNack(anyLong(), eq(false), eq(true));
        verify(retryStateService, never()).resetRetryCount("retry-not-max-ioexception");
    }

    @Test
    void testRetryOrMoveToDlq_NotYetMaxRetries() throws IOException {
        // Arrange
        Message message = createTestMessage("retry-message-id");

        when(retryStateService.getRetryCount("retry-message-id")).thenReturn(new AtomicInteger(2));
        when(retryStateService.isMaxRetriesExceeded("retry-message-id", 3)).thenReturn(false);
        when(channel.isOpen()).thenReturn(true);

        // Act
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Assert
        verify(channel, times(1)).basicNack(anyLong(), eq(false), eq(true));
        verify(retryStateService, never()).resetRetryCount("retry-message-id");
    }

    @Test
    void testMoveToDlq_ChannelClosed() throws IOException {
        Message message = createTestMessage("dlq-closed-channel-id");

        when(channel.isOpen()).thenReturn(false);

        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        verify(channel, never()).basicReject(anyLong(), anyBoolean());
    }

    @Test
    void testMoveToDlq_ChannelOpen() throws IOException {
        // Given
        Message message = createTestMessage("dlq-open");
        when(channel.isOpen()).thenReturn(true);
        when(retryStateService.getRetryCount("dlq-open")).thenReturn(new AtomicInteger(3));
        when(retryStateService.isMaxRetriesExceeded("dlq-open", 3)).thenReturn(true);

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        verify(channel, times(1)).basicReject(anyLong(), eq(false)); // moveToDlq에서 호출
        verify(retryStateService, times(1)).resetRetryCount("dlq-open");
    }

    @Test
    void testMoveToDlq_IOException() throws IOException {
        // Given
        Message message = createTestMessage("dlq-ioexception");
        when(channel.isOpen()).thenReturn(true);
        when(retryStateService.getRetryCount("dlq-ioexception")).thenReturn(new AtomicInteger(3));
        when(retryStateService.isMaxRetriesExceeded("dlq-ioexception", 3)).thenReturn(true);
        doThrow(new IOException("Test IO Exception")).when(channel).basicReject(anyLong(), eq(false));

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        verify(channel, times(1)).basicReject(anyLong(), eq(false)); // moveToDlq에서 호출
        verify(retryStateService, times(1)).resetRetryCount("dlq-ioexception");
    }



    @Test
    void testAcknowledgeMessageIOException() throws IOException {
        // Given
        Message message = createTestMessage("ioexception-message-id");
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon)); // Coupon 존재
        when(channel.isOpen()).thenReturn(true); // 채널 열림

        doThrow(new IOException("Test IO Exception")).when(channel).basicAck(anyLong(), eq(false));

        // When
        couponMessageListener.handleCouponAssignRequest(new CouponAssignRequestDTO(1L, 123L), message, channel);

        // Then
        verify(channel, times(1)).basicAck(anyLong(), eq(false)); // basicAck 호출 확인
        verify(retryStateService, never()).resetRetryCount(anyString()); // retryStateService는 호출되지 않음
    }


    private Message createTestMessage(String messageId) {
        MessageProperties properties = new MessageProperties();
        properties.setMessageId(messageId);
        return new Message("{\"test\":\"message\"}".getBytes(), properties);
    }
}
