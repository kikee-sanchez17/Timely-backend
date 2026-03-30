package dev.esanchez.timely.backend.exception;

public class SubserviceNotFoundException extends RuntimeException {

    public SubserviceNotFoundException(Long subserviceId) {
        super("Subservice not found with id: " + subserviceId);
    }
}