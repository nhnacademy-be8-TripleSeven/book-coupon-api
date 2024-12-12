package com.nhnacademy.bookapi.exception;

public class TagAlreadyExistException extends RuntimeException {
    public TagAlreadyExistException(String message) {
        super(message);
    }
}
