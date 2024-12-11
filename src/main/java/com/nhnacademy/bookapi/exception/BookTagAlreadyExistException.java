package com.nhnacademy.bookapi.exception;

public class BookTagAlreadyExistException extends RuntimeException {
    public BookTagAlreadyExistException(String message) {
        super(message);
    }
}
