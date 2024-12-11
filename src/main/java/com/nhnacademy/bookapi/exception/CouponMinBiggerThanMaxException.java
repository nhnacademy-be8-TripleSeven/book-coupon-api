package com.nhnacademy.bookapi.exception;

public class CouponMinBiggerThanMaxException extends RuntimeException {
    public CouponMinBiggerThanMaxException(String message) {
        super(message);
    }
}
