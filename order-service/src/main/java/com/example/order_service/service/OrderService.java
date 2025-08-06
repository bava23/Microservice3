package com.example.order_service.service;

import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Place Order
    public OrderResponseDTO placeOrder(Order order) {

        // Fetch product details from Product Service
        ProductDTO productDTO = webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/products/" + order.getProductId())
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block(); // blocking here for simplicity, can be reactive

        // Save order to DB
        orderRepository.save(order);

        // Build response
        return mapToOrderResponse(order, productDTO);

    }

    // Get All Orders
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            ProductDTO productDTO = null;
            try {
                // Call product service to get product details
                productDTO = webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8081/products/" + order.getProductId())
                        .retrieve()
                        .bodyToMono(ProductDTO.class)
                        .block();
            } catch (Exception e) {
                System.err.println("Error fetching product for orderId: " + order.getId());
            }
            // Map order and product to response
            responseList.add(mapToOrderResponse(order, productDTO));
        }
        return responseList;
    }
    private OrderResponseDTO mapToOrderResponse(Order order, ProductDTO productDTO) {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getId());
        response.setProductId(order.getProductId());
        response.setQuantity(order.getQuantity());
        if (productDTO != null) {
            response.setProductName(productDTO.getName());
            response.setProductPrice(productDTO.getPrice());
            response.setTotalPrice(productDTO.getPrice() * order.getQuantity());
        } else {
            response.setProductName("Unknown");
            response.setProductPrice(0.0);
            response.setTotalPrice(0.0);
        }
        return response;
    }
}

