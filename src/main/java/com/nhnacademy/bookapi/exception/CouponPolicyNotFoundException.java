package com.nhnacademy.bookapi.exception;

public class CouponPolicyNotFoundException extends RuntimeException {
    public CouponPolicyNotFoundException(String message) {
        super(message);
    }
}
