package com.nhnacademy.bookapi.exception;

public class ReviewAlreadyExistException extends RuntimeException {
    public ReviewAlreadyExistException(String message) {
        super(message);
    }
}
