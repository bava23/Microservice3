package com.example.order_service.service;

import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.ProductDTO;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

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

        // Mock WebClient chain — using casting to avoid generic compile issues
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
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
        // Simulate product service failure
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
}
