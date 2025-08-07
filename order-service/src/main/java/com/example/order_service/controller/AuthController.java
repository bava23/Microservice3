package com.example.order_service.controller;

import com.example.order_service.dto.LoginRequest;
import com.example.order_service.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return jwtUtil.generateToken(
                loginRequest.getUsername(),
                loginRequest.getRole().toUpperCase()
        );
    }

}
