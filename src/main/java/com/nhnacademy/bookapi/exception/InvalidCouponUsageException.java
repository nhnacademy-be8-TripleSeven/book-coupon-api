package com.nhnacademy.bookapi.exception;

public class InvalidCouponUsageException extends RuntimeException {
    public InvalidCouponUsageException(String message) {
        super(message);
    }
}
