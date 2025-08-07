package com.example.product_service.controller;

import com.example.product_service.dto.LoginRequest;
import com.example.product_service.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest loginRequest) {
        // In real-world: validate username/password from DB
        return jwtUtil.generateToken(loginRequest.getUsername(), loginRequest.getRole().toUpperCase());
    }
}
