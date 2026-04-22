package dev.esanchez.timely.backend.core.globalException.customGlobalException;

public class CustomValidationException extends RuntimeException {
    public CustomValidationException(String message) {
        super(message);
    }
}
