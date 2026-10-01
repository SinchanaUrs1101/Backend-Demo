package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // VALIDATION ERRORS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        var fieldError = ex.getBindingResult().getFieldError();

        ErrorResponse response = new ErrorResponse();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");

        if (fieldError != null) {
            response.setField(fieldError.getField());
            response.setError(fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // RESOURCE NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ErrorResponse response = new ErrorResponse();

        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());
        response.setError(ex.getErrorCode());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // DUPLICATE RESOURCE
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex) {

        ErrorResponse response = new ErrorResponse();

        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(ex.getMessage());
        response.setError("DUPLICATE_RESOURCE");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // BUSINESS RULE VIOLATIONS
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex) {

        ErrorResponse response = new ErrorResponse();

        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(ex.getMessage());
        response.setError("CONFLICT");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // INVALID ARGUMENTS
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ErrorResponse response = new ErrorResponse();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        response.setError("INVALID_ARGUMENT");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // GENERIC EXCEPTION
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        ErrorResponse response = new ErrorResponse();

        response.setStatus(
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        response.setMessage(
                "An unexpected error occurred"
        );

        response.setError(
                "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}