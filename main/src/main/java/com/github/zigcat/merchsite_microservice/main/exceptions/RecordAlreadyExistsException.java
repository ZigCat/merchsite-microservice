package com.github.zigcat.merchsite_microservice.main.exceptions;

public class RecordAlreadyExistsException extends Exception{
    public RecordAlreadyExistsException() {
        super("Record already exists (400)");
    }

    public RecordAlreadyExistsException(String entity) {
        super(entity + " with these attributes already exists (400)");
    }

    public RecordAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
