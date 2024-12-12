package com.nhnacademy.bookapi.exception;

public class CouponAlreadyUsedExceeption extends RuntimeException {
    public CouponAlreadyUsedExceeption(String message) {
        super(message);
    }
}
