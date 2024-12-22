package com.nhnacademy.bookapi.exception;

public class WrapperAlreadyExistException extends RuntimeException {
    public WrapperAlreadyExistException(String message) {
        super(message);
    }
}
