package dev.esanchez.timely.backend.core.handleException.customHandleException;

public class CustomerNotAuthenticatedException extends RuntimeException {

    public CustomerNotAuthenticatedException() {
        super("Customer is not authenticated");
    }
}