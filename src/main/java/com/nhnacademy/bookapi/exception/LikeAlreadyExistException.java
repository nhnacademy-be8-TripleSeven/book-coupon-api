package com.nhnacademy.bookapi.exception;

public class LikeAlreadyExistException extends RuntimeException {
    public LikeAlreadyExistException(String message) {
        super(message);
    }
}
