package com.nhnacademy.bookapi.exception;

public class WrappableAlreadyExistException extends RuntimeException {
    public WrappableAlreadyExistException(String message) {
        super(message);
    }
}
