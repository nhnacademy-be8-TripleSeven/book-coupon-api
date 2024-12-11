package com.nhnacademy.bookapi.exception;

public class BookTagNotFoundException extends RuntimeException {
    public BookTagNotFoundException(String message) {
        super(message);
    }
}
