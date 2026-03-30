package dev.esanchez.timely.backend.exception;

public class CustomerNotAuthenticatedException extends RuntimeException {

    public CustomerNotAuthenticatedException() {
        super("Customer is not authenticated");
    }
}