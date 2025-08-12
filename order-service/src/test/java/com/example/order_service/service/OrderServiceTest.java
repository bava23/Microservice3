package com.example.order_service.service;

import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.security.InternalTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private InternalTokenService internalTokenService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample Order
        order = new Order();
        order.setId(1L);
        order.setProductId(101L);
        order.setQuantity(2);

        // Sample ProductDTO
        productDTO = new ProductDTO();
        productDTO.setId(101L);
        productDTO.setName("Test Product");
        productDTO.setPrice(100.0);

        // Mock token service
        when(internalTokenService.getServiceToken()).thenReturn("dummy-token");

        // Mock WebClient chain
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.header(eq("Authorization"), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(productDTO));
    }

    @Test
    void testPlaceOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO response = orderService.placeOrder(order);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(101L, response.getProductId());
        assertEquals(2, response.getQuantity());
        assertEquals("Test Product", response.getProductName());
        assertEquals(100.0, response.getProductPrice());
        assertEquals(200.0, response.getTotalPrice());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponseDTO> responseList = orderService.getAllOrders();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        OrderResponseDTO response = responseList.get(0);
        assertEquals("Test Product", response.getProductName());
        assertEquals(200.0, response.getTotalPrice());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetAllOrders_WhenProductServiceFails() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(responseSpec.bodyToMono(ProductDTO.class)).thenReturn(Mono.error(new RuntimeException("Service down")));

        List<OrderResponseDTO> responseList = orderService.getAllOrders();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        OrderResponseDTO response = responseList.get(0);
        assertEquals("Unknown", response.getProductName());
        assertEquals(0.0, response.getProductPrice());
        assertEquals(0.0, response.getTotalPrice());
    }
    @Test
    void testUpdateOrder() {
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setProductId(101L);
        updatedOrder.setQuantity(5);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderResponseDTO response = orderService.updateOrder(1L, updatedOrder);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(5, response.getQuantity());
        assertEquals(500.0, response.getTotalPrice());
        verify(orderRepository, times(1)).save(updatedOrder);
    }
    @Test
    void testDeleteOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).delete(order);
    }

}
