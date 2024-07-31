package com.github.zigcat.merchsite_microservice.main.exceptions;

public class AuthenticationErrorException extends Exception{
    public AuthenticationErrorException() {
        super("Unauthorized access (401)");
    }

    public AuthenticationErrorException(String entity) {
        super("Unauthorized access to " + entity + " (401)");
    }

    public AuthenticationErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
