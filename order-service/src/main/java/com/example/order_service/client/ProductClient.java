package com.example.order_service.client;

import com.example.order_service.dto.ProductDTO;
import com.example.order_service.exception.ProductServiceUnavailable;
import com.example.order_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://product-service").build(); // Eureka name or service URL
    }

    public ProductDTO getProductById(Long id) {
        try {
            return webClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            response -> Mono.error(new ResourceNotFoundException("Product with ID " + id + " not found")))
                    .onStatus(status -> status.is5xxServerError(),
                            response -> Mono.error(new ProductServiceUnavailable("Product Service returned 5xx error")))
                    .bodyToMono(ProductDTO.class)
                    .block();
        } catch (Exception ex) {
            throw new ProductServiceUnavailable("Product Service is not reachable");
        }
    }
}
