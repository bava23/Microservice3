package com.example.product_service.service;

import com.example.product_service.entity.Product;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------- Add Product -----------------
    @Test
    void testAddProduct() {
        Product laptop = new Product();
        laptop.setId(1L);
        laptop.setName("Laptop");
        laptop.setPrice(75000.0);

        when(productRepository.save(any(Product.class))).thenReturn(laptop);

        Product saved = productService.addProduct(laptop);

        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        assertEquals(75000.0, saved.getPrice());
        verify(productRepository, times(1)).save(laptop);
    }

    // ----------------- Get All Products -----------------
    @Test
    void testGetAllProducts() {
        Product laptop = new Product(1L, "Laptop", 75000.0);
        Product phone = new Product(2L, "Phone", 50000.0);

        when(productRepository.findAll()).thenReturn(Arrays.asList(laptop, phone));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    // ----------------- Get Product by ID -----------------
    @Test
    void testGetProductById_Found() {
        Product laptop = new Product(1L, "Laptop", 75000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));

        Product found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals("Laptop", found.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository, times(1)).findById(99L);
    }

    // ----------------- Update Product -----------------
    @Test
    void testUpdateProduct_Found() {
        Product existingLaptop = new Product(1L, "Laptop", 75000.0);
        Product updatedLaptop = new Product(1L, "Gaming Laptop", 90000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingLaptop));
        when(productRepository.save(any(Product.class))).thenReturn(updatedLaptop);

        Product result = productService.updateProduct(1L, updatedLaptop);

        assertEquals("Gaming Laptop", result.getName());
        assertEquals(90000.0, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(existingLaptop);
    }

    @Test
    void testUpdateProduct_NotFound() {
        Product updatedLaptop = new Product(1L, "Gaming Laptop", 90000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updatedLaptop));
        verify(productRepository, times(1)).findById(1L);
    }

    // ----------------- Delete Product -----------------
    @Test
    void testDeleteProduct_Found() {
        Product laptop = new Product(1L, "Laptop", 75000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
        doNothing().when(productRepository).delete(laptop);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(laptop);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }
}
