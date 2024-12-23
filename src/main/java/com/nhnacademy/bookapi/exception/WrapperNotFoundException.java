package com.nhnacademy.bookapi.exception;

public class WrapperNotFoundException extends RuntimeException {
    public WrapperNotFoundException(String message) {
        super(message);
    }
}
