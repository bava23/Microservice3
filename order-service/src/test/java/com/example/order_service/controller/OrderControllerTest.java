package com.example.order_service.controller;

import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean// ✅ Mock the service in Spring Boot 3.4+
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order order;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        // Sample Order entity
        order = new Order();
        order.setId(1L);
        order.setProductId(101L);
        order.setQuantity(2);

        // Sample OrderResponseDTO matching your class
        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setOrderId(1L);
        orderResponseDTO.setProductId(101L);
        orderResponseDTO.setQuantity(2);
        orderResponseDTO.setTotalPrice(200.0);
        orderResponseDTO.setProductName("Test Product");
        orderResponseDTO.setProductPrice(100.0);
    }

    @Test
    void testPlaceOrder() throws Exception {
        Mockito.when(orderService.placeOrder(any(Order.class))).thenReturn(orderResponseDTO);

        mockMvc.perform(post("/orders/placeOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.productId").value(101L))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(200.0))
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.productPrice").value(100.0));

        verify(orderService, times(1)).placeOrder(any(Order.class));
    }

    @Test
    void testGetAllOrders() throws Exception {
        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(orderResponseDTO));

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].productId").value(101L))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].totalPrice").value(200.0))
                .andExpect(jsonPath("$[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[0].productPrice").value(100.0));

        verify(orderService, times(1)).getAllOrders();
    }
}

