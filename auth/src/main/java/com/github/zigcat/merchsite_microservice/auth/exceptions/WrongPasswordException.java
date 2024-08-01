package com.github.zigcat.merchsite_microservice.auth.exceptions;

public class WrongPasswordException extends Exception{
    public WrongPasswordException() {
        super("Passwords doesn't match");
    }

    public WrongPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
