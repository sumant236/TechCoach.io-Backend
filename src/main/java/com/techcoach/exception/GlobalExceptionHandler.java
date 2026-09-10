package com.techcoach.exception;

import com.techcoach.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler to intercept application exceptions
 * and format them into the standard ApiResponse structure for frontend consumption.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Extracts field-specific validation errors from @Valid constraints and maps them into the response errors map
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Validation Failed")
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Maps known business conflict scenarios (e.g., duplicate email registration) to HTTP 409 Conflict
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessExceptions(UserAlreadyExistsException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .success(false)
                .message("User Already Exists")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Intercepts unauthorized access attempts to return a clean HTTP 401 response
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .success(false)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Handles missing resource scenarios gracefully with HTTP 404 Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .success(false)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Generic fallback handler for all other unhandled business logic exceptions, defaulting to HTTP 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessExceptions(RuntimeException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
