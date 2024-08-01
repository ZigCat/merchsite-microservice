package com.github.zigcat.merchsite_microservice.auth.exceptions;

public class RecordNotFoundException extends Exception{
    public RecordNotFoundException() {
        super("Record of entity not found");
    }

    public RecordNotFoundException(String entity) {
        super(entity + " not found");
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
