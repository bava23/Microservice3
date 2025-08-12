package com.example.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Product name cannot be blank")
    @Pattern(regexp = ".*[a-zA-Z]+.*", message = "Product name must contain alphabetic characters")
    private String name;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    private Double price;
}
