package com.nhnacademy.bookapi.exception;

public class CouponAlreadyAssignedException extends RuntimeException {
    public CouponAlreadyAssignedException(String message) {
        super(message);
    }
}
