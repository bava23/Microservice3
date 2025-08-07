package com.example.order_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InternalTokenService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private String cachedToken;
    private long tokenFetchTime;

    public String getServiceToken() {
        // Refresh token every 50 minutes
        if (cachedToken == null || (System.currentTimeMillis() - tokenFetchTime) > (50 * 60 * 1000)) {
            cachedToken = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/internal/token") // product-service endpoint
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            tokenFetchTime = System.currentTimeMillis();
        }
        return "Bearer " + cachedToken;
    }
}
