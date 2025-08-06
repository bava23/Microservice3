package com.example.order_service.controller;

import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Orders", description = "APIs for managing orders") // ✅ Swagger tag
@Slf4j // ✅ Lombok logger
@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Place a new order
    @Operation(summary = "Place a new order", description = "Creates a new order in the system")
    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody Order order) {
        log.info("Placing new order | productId={} | quantity={}", order.getProductId(), order.getQuantity());

        OrderResponseDTO response = orderService.placeOrder(order);

        log.info("Order placed successfully | orderId={} | totalPrice={}",
                response.getOrderId(), response.getTotalPrice());

        return ResponseEntity.ok(response);
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.info("Fetching all orders");

        List<OrderResponseDTO> orders = orderService.getAllOrders();

        log.info("Fetched {} orders", orders.size());

        return ResponseEntity.ok(orders);
    }
}
