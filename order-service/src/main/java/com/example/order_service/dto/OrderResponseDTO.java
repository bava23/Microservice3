package com.example.order_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponseDTO {

    private Long OrderId;
    private Long productId;
    private int quantity;
    private double totalPrice;

    // product details
    private String productName;
    private double productPrice;

}

