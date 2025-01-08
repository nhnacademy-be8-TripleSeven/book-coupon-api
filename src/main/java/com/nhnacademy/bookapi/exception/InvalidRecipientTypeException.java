package com.nhnacademy.bookapi.exception;

public class InvalidRecipientTypeException extends RuntimeException {
    public InvalidRecipientTypeException(String message) {
        super(message);
    }
}
