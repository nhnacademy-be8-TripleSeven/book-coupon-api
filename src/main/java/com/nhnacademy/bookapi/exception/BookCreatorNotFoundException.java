package com.nhnacademy.bookapi.exception;

public class BookCreatorNotFoundException extends RuntimeException {

    public BookCreatorNotFoundException(String message) {
        super(message);
    }
}
