package com.example.product_service.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
// This exception is thrown when a requested resource is not found in the system.