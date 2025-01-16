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

    @Captor
    private ArgumentCaptor<Long> deliveryTagCaptor;

    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CouponPolicy policy = CouponPolicy.builder()
                .name("Test Policy")
                .couponMinAmount(0L)
                .couponMaxAmount(100L)
                .couponDiscountRate(BigDecimal.valueOf(10))
                .couponDiscountAmount(null)
                .couponValidTime(7)
                .build();

        testCoupon = Coupon.builder()
                .id(1L)
                .name("Test Coupon")
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .build();
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

    private Message createTestMessage(String messageId) {
        MessageProperties properties = new MessageProperties();
        properties.setMessageId(messageId);
        return new Message("{\"test\":\"message\"}".getBytes(), properties);
    }
}
