package com.example.product_service.controller;

import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // converts Java objects <-> JSON

    @MockitoBean
    private ProductService productService;

    // 1️⃣ Test create product
    @Test
    void testAddProduct() throws Exception {
        Product product = new Product(1L, "Laptop", 1500.0);
        Mockito.when(productService.addProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0));
    }

    // 2️⃣ Test get all products
    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(
                new Product(1L, "Laptop", 1500.0),
                new Product(2L, "Phone", 800.0)
        );
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    // 3️⃣ Test get product by ID
    @Test
    void testGetProductById() throws Exception {
        Product product = new Product(1L, "Laptop", 1500.0);
        Mockito.when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    // 4️⃣ Test validation error for negative ID
    @Test
    void testGetProductById_InvalidId() throws Exception {
        mockMvc.perform(get("/products/-1"))
                .andExpect(status().isBadRequest()); // triggers ConstraintViolationException
    }

}

// 5️⃣ Test
