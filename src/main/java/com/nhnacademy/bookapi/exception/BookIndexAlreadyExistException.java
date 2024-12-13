package com.nhnacademy.bookapi.exception;

public class BookIndexAlreadyExistException extends RuntimeException {
    public BookIndexAlreadyExistException(String message) {
        super(message);
    }
}
