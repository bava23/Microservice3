package com.example.product_service.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
    }

    // Validation (DTO fields)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // PathVariable / RequestParam validations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    // JSON Parsing Errors
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseErrors(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        Throwable cause = ex.getCause();

        if (cause instanceof MismatchedInputException mismatched) {
            String fieldName = mismatched.getPath().stream()
                    .findFirst()
                    .map(ref -> ref.getFieldName())
                    .orElse("unknown");

            if ("name".equals(fieldName)) {
                errors.put("name", "Product name must be a string");
            } else {
                errors.put("error", "Invalid data type for field: " + fieldName);
            }
        } else if (cause instanceof InvalidFormatException ife) {
            String field = ife.getPath().get(0).getFieldName();
            errors.put("error", "Invalid format for field: " + field);
        } else {
            errors.put("error", "Malformed JSON or invalid request data");
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Global Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    // Common builder
    private ResponseEntity<Object> buildError(HttpStatus status, String error, String message, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}
