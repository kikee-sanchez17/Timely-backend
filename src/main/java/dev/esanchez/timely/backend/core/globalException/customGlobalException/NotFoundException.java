package dev.esanchez.timely.backend.core.globalException.customGlobalException;

import org.springframework.stereotype.Component;


@Component
public class NotFoundException extends RuntimeException {

    public NotFoundException() {}

    public NotFoundException(String msg , Long employeeId) {
        super( msg + employeeId);
    }
}