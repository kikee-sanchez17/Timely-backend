package dev.esanchez.timely.backend.core.globalException.customGlobalException;

import dev.esanchez.timely.backend.core.handleException.customHandleException.BadRequestException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.CustomerNotAuthenticatedException;
import dev.esanchez.timely.backend.core.handleException.customHandleException.AlreadyExistsException;
import dev.esanchez.timely.backend.module.shared.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores de validación de Spring y los convierte en tu excepción
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

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

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidation(CustomValidationException ex) {
         return buildError(400, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildError(int status, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(status, message, LocalDateTime.now())
        );
    }


}