package com.github.zigcat.merchsite_microservice.main.exceptions;

public class AuthServerErrorException extends Exception {
    public AuthServerErrorException() {
        super("Auth server error occurred (500)");
    }

    public AuthServerErrorException(String message) {
        super(message);
    }

    public AuthServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
