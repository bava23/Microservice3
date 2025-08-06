package com.example.order_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Product ID must be required")
    @Min(value = 1, message = "Product ID must be positive")
    private Long productId;

    @NotNull(message = "quantity must be required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
