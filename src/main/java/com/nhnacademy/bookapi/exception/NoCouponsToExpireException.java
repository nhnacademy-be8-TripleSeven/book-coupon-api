package com.nhnacademy.bookapi.exception;

public class NoCouponsToExpireException extends RuntimeException {
    public NoCouponsToExpireException(String message) {
        super(message);
    }
}
