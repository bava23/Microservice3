package com.example.product_service.controller;

import com.example.product_service.dto.ProductRequestDTO;
import com.example.product_service.entity.Product;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Validated
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create a product
    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        log.info("Request to add product: {}", productRequestDTO.getName());

        Product product = ProductMapper.toEntity(productRequestDTO);
        Product savedProduct = productService.addProduct(product);

        log.info("Product created with ID: {}", savedProduct.getId());
        return ResponseEntity.ok(savedProduct);
    }

    // Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Request to get all products");

        List<Product> products = productService.getAllProducts();

        log.info("Total products retrieved: {}", products.size());
        return products;
    }

    // Get product by ID
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductByID(
            @PathVariable @Positive(message = "Product ID must be positive") Long productId) {

        log.info("Request to get product by ID: {}", productId);

        Product product = productService.getProductById(productId);

        log.debug("Retrieved product details: {}", product);
        return ResponseEntity.ok(product);
    }

    // Update product
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequestDTO updatedProductDTO) {

        log.info("Request to update product with ID: {}", productId);

        Product updatedProduct = ProductMapper.toEntity(updatedProductDTO);
        Product product = productService.updateProduct(productId, updatedProduct);

        log.info("Product updated successfully: {}", product.getId());
        return ResponseEntity.ok(product);
    }

    // Delete product
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        log.info("Request to delete product with ID: {}", productId);

        productService.deleteProduct(productId);

        log.info("Product deleted successfully: {}", productId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }
}
