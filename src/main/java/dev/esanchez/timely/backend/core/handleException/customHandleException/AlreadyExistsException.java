package dev.esanchez.timely.backend.core.handleException.customHandleException;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String entity) {
        super(entity + " already exists");
    }
}