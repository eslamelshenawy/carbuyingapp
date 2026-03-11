package com.carbuyingapp.exception;

public class InvalidRequestStatusException extends RuntimeException {

    public InvalidRequestStatusException(String message) {
        super(message);
    }
}
