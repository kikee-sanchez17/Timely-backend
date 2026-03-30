package dev.esanchez.timely.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEmailExists(EmailAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(CustomerNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleCustomerNotAuthenticated(CustomerNotAuthenticatedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEmployeeNotFound(EmployeeNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(SubserviceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleSubserviceNotFound(SubserviceNotFoundException ex) {
        return ex.getMessage();
    }
}