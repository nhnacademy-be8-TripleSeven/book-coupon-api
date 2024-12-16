package com.nhnacademy.bookapi.exception;

public class CouponNotAssignedException extends RuntimeException {
    public CouponNotAssignedException(String message) {
        super(message);
    }
}
