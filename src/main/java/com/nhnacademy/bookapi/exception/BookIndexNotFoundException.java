package com.nhnacademy.bookapi.exception;

public class BookIndexNotFoundException extends RuntimeException {

    public BookIndexNotFoundException(String message) {
        super(message);
    }
}
