package com.example.order_service.exception;

public class ProductServiceUnavailable extends RuntimeException {
    public ProductServiceUnavailable(String message) {
        super(message);
    }
}
