package com.example.order_service.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        body.put("errors", fieldErrors);

        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // Handle @RequestParam or @PathVariable validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Constraint violation");
        body.put("errors", ex.getConstraintViolations().toString());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle bad JSON formatting
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidJson(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Malformed JSON request");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle WebClient errors when calling external services
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientError(WebClientResponseException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("message", "Error calling Product Service");

        // Try extracting just the message field from the JSON body
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(ex.getResponseBodyAsString());
            String errorMessage = root.has("message") ? root.get("message").asText() : ex.getResponseBodyAsString();
            body.put("error", errorMessage);
        } catch (Exception e) {
            body.put("error", ex.getResponseBodyAsString()); // fallback
        }

        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    // Handle Product Service Down or Unavailable
    @ExceptionHandler(ProductServiceUnavailable.class)
    public ResponseEntity<Object> handleProductServiceDown(ProductServiceUnavailable ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Handle Product Service DOWN (no response at all)
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Object> handleWebClientRequestException(WebClientRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("message", "Product Service is currently unavailable. Please try again later.");
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }



    // Handle database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDatabaseError(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("message", "Database error");
        body.put("error", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Handle all unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", "An unexpected error occurred");
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
