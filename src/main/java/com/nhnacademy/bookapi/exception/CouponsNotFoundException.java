package com.nhnacademy.bookapi.exception;

public class CouponsNotFoundException extends RuntimeException {
    public CouponsNotFoundException(String message) {
        super(message);
    }
}
