package com.nhnacademy.bookapi.exception;

public class WrappableNotFoundException extends RuntimeException {
    public WrappableNotFoundException(String message) {
        super(message);
    }
}
