package com.nhnacademy.bookapi.service.coupon.consumer;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RetryStateService {
    private final ConcurrentHashMap<String, AtomicInteger> retryCounts = new ConcurrentHashMap<>();

    public AtomicInteger getRetryCount(String messageId) {
        return retryCounts.computeIfAbsent(messageId, id -> new AtomicInteger(0));
    }

    public void resetRetryCount(String messageId) {
        retryCounts.remove(messageId);
    }

    public boolean isMaxRetriesExceeded(String messageId, int maxRetryCount) {
        return getRetryCount(messageId).get() > maxRetryCount;
    }
}
