package com.example.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{2,29}$",
            message = "Username must start with a letter and contain only letters, numbers, or underscores (3-30 characters)")
    private String username;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be either USER or ADMIN")
    private String role;
}
