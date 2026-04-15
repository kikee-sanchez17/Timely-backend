package dev.esanchez.timely.backend.core.globalException.customGlobalException;

import dev.esanchez.timely.backend.core.handleException.customHandleException.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.EmailAlreadyExistsException;
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

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex) {

        return ex.getMessage();
    }

}