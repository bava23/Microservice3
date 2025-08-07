package com.example.order_service.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SwaggerDownloadController {

    private static final String API_DOCS_URL = "http://localhost:8082/v3/api-docs.yaml?group=order-service";

    @GetMapping(value = "/swagger.yaml", produces = "application/x-yaml")
    public String getSwaggerYaml() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(API_DOCS_URL, String.class);
    }

    @GetMapping(value = "/swagger.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSwaggerJson() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                API_DOCS_URL.replace(".yaml", ""), // convert to JSON version
                String.class
        );
    }
}
