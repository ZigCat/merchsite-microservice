package com.github.zigcat.merchsite_microservice.main.exceptions;

public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
        super("Record of entity not found (404)");
    }

    public RecordNotFoundException(String entity) {
        super(entity + " not found (404)");
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
