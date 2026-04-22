package dev.esanchez.timely.backend.core.handleException.customHandleException;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
