package com.github.zigcat.merchsite_microservice.auth.exceptions;

public class WrongJwtException extends Exception{
    public WrongJwtException() {
        super("JWT token is invalid or absent");
    }

    public WrongJwtException(String message) {
        super(message);
    }

    public WrongJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
