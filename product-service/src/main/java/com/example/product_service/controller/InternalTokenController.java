package com.example.product_service.controller;

import com.example.product_service.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class InternalTokenController {

    private final JwtUtil jwtUtil;

    public InternalTokenController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/token")
    public String getInternalServiceToken() {
        // Generate a token for internal calls with ADMIN role
        return jwtUtil.generateToken("internal-service", "ADMIN");
    }
}
