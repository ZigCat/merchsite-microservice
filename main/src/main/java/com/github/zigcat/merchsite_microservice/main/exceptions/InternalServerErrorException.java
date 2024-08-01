package com.github.zigcat.merchsite_microservice.main.exceptions;

public class InternalServerErrorException extends Exception{
    public InternalServerErrorException() {
        super("Internal server error occurred (500)");
    }

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
