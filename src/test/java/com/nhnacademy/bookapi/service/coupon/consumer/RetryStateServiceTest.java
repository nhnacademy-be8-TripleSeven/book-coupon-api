package com.nhnacademy.bookapi.service.coupon.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryStateServiceTest {
    private RetryStateService retryStateService;

    @BeforeEach
    void setUp() {
        retryStateService = new RetryStateService();
    }

    @Test
    void testGetRetryCount_FirstTime() {
        // Given
        String messageId = "test-message-id";

        // When
        AtomicInteger retryCount = retryStateService.getRetryCount(messageId);

        // Then
        assertNotNull(retryCount);
        assertEquals(0, retryCount.get(), "Retry count for a new message ID should be 0");
    }

    @Test
    void testGetRetryCount_Increment() {
        // Given
        String messageId = "test-message-id";

        // When
        AtomicInteger retryCount = retryStateService.getRetryCount(messageId);
        retryCount.incrementAndGet();

        // Then
        assertEquals(1, retryStateService.getRetryCount(messageId).get(), "Retry count should be incremented to 1");
    }

    @Test
    void testResetRetryCount() {
        // Given
        String messageId = "test-message-id";
        retryStateService.getRetryCount(messageId).incrementAndGet(); // Increment to 1

        // When
        retryStateService.resetRetryCount(messageId);

        // Then
        assertEquals(0, retryStateService.getRetryCount(messageId).get(), "Retry count should reset to 0");
    }

    @Test
    void testIsMaxRetriesExceeded_True() {
        // Given
        String messageId = "test-message-id";
        retryStateService.getRetryCount(messageId).set(5); // Set retry count to 5
        int maxRetryCount = 3;

        // When
        boolean exceeded = retryStateService.isMaxRetriesExceeded(messageId, maxRetryCount);

        // Then
        assertTrue(exceeded, "isMaxRetriesExceeded should return true if retry count exceeds the max retry count");
    }

    @Test
    void testIsMaxRetriesExceeded_False() {
        // Given
        String messageId = "test-message-id";
        retryStateService.getRetryCount(messageId).set(2); // Set retry count to 2
        int maxRetryCount = 3;

        // When
        boolean exceeded = retryStateService.isMaxRetriesExceeded(messageId, maxRetryCount);

        // Then
        assertFalse(exceeded, "isMaxRetriesExceeded should return false if retry count does not exceed the max retry count");
    }
}
