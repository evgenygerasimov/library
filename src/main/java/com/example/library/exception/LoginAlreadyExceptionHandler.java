package com.example.library.exception;

public class LoginAlreadyExceptionHandler extends RuntimeException {
    public LoginAlreadyExceptionHandler(String message) {
        super(message);
    }
}
