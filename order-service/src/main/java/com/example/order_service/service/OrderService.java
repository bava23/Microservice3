package com.example.order_service.service;

import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.entity.Order;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.security.InternalTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private InternalTokenService internalTokenService;

    // ✅ Fetch product details with internal token
    private ProductDTO fetchProductDetails(Long productId) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/products/" + productId)
                .header("Authorization", internalTokenService.getServiceToken()) // internal token added
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .block();
    }

    // ✅ Place Order
    public OrderResponseDTO placeOrder(Order order) {
        ProductDTO productDTO = fetchProductDetails(order.getProductId());

        orderRepository.save(order);

        return mapToOrderResponse(order, productDTO);
    }

    // ✅ Get All Orders
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponseDTO> responseList = new ArrayList<>();

        for (Order order : orders) {
            ProductDTO productDTO = null;
            try {
                productDTO = fetchProductDetails(order.getProductId());
            } catch (Exception e) {
                System.err.println("Error fetching product for orderId: " + order.getId());
            }
            responseList.add(mapToOrderResponse(order, productDTO));
        }
        return responseList;
    }

    // ✅ Update Order
    public OrderResponseDTO updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        existingOrder.setProductId(updatedOrder.getProductId());
        existingOrder.setQuantity(updatedOrder.getQuantity());

        orderRepository.save(existingOrder);

        ProductDTO productDTO = null;
        try {
            productDTO = fetchProductDetails(updatedOrder.getProductId());
        } catch (Exception e) {
            System.err.println("Error fetching product for updated orderId: " + id);
        }

        return mapToOrderResponse(existingOrder, productDTO);
    }

    // ✅ Delete Order
    public void deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        orderRepository.delete(existingOrder);
    }

    // ✅ Map Order + Product Info
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
