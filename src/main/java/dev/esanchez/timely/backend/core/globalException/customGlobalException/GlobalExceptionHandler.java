package dev.esanchez.timely.backend.core.globalException.customGlobalException;

import dev.esanchez.timely.backend.core.handleException.customHandleException.BadRequestException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.AlreadyExistsException;
import dev.esanchez.timely.backend.module.shared.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException ex) {
        return buildError(409, ex.getMessage());
    }

    @ExceptionHandler(CustomerNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotAuthenticated(CustomerNotAuthenticatedException ex) {
        return buildError(401, ex.getMessage());

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return buildError(404, ex.getMessage());

    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildError(400, ex.getMessage());

    }

    private ResponseEntity<ErrorResponse> buildError(int status, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(status, message, LocalDateTime.now())
        );
    }


}