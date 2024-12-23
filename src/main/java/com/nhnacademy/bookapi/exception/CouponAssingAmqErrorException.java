package com.nhnacademy.bookapi.exception;

public class CouponAssingAmqErrorException extends RuntimeException {
    public CouponAssingAmqErrorException(String message) {
        super(message);
    }
    public CouponAssingAmqErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
