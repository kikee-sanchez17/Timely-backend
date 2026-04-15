package dev.esanchez.timely.backend.core.globalException.customGlobalException;

import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg , Long employeeId) {
        super( msg + employeeId);
    }
}