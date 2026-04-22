package dev.esanchez.timely.backend.core.globalException.customGlobalException;

public class MethodArgumentNotValidException extends RuntimeException {
    public MethodArgumentNotValidException(String message) {
        super(message);
    }
}
