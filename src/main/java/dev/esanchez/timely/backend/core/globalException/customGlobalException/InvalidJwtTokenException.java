package dev.esanchez.timely.backend.core.globalException.customGlobalException;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
