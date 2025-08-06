package com.example.product_service.service;

import com.example.product_service.entity.Product;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(Product product) {
        log.info("Adding new product: {}", product.getName());
        log.debug("Product details before save: {}", product);

        Product savedProduct = productRepository.save(product);

        log.info("Product saved with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        log.info("Fetching all products");

        List<Product> products = productRepository.findAll();

        log.debug("Total products found: {}", products.size());
        return products;
    }

    public Product getProductById(Long productId) {
        log.info("Fetching product with ID: {}", productId);

        return productRepository.findById(productId)
                .map(product -> {
                    log.debug("Product found: {}", product);
                    return product;
                })
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException("Product not found with ID: " + productId);
                });
    }

    public Product updateProduct(Long productId, Product updatedProduct) {
        log.info("Updating product with ID: {}", productId);
        log.debug("New product details: {}", updatedProduct);

        Product existingProduct = getProductById(productId);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());

        Product savedProduct = productRepository.save(existingProduct);

        log.info("Product updated successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);

        Product product = getProductById(productId);
        productRepository.delete(product);

        log.info("Product deleted successfully: {}", productId);
    }
}
