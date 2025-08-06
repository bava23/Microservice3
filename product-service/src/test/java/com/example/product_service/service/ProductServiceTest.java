package com.example.product_service.service;

import com.example.product_service.entity.Product;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleProduct = new Product(1L, "Laptop", 1500.0);
    }

    // 1️⃣ Test addProduct
    @Test
    void testAddProduct() {
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);

        Product saved = productService.addProduct(sampleProduct);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).save(sampleProduct);
    }

    // 2️⃣ Test getAllProducts
    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> products = productService.getAllProducts();

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findAll();
    }

    // 3️⃣ Test getProductById - Found
    @Test
    void testGetProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product found = productService.getProductById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).findById(1L);
    }

    // 4️⃣ Test getProductById - Not Found
    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));

        assertThat(ex.getMessage()).isEqualTo("Product not found with ID: 1");
        verify(productRepository, times(1)).findById(1L);
    }

    // 5️⃣ Test updateProduct
    @Test
    void testUpdateProduct() {
        Product updatedData = new Product(1L, "Updated Laptop", 2000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedData);

        Product updated = productService.updateProduct(1L, updatedData);

        assertThat(updated.getName()).isEqualTo("Updated Laptop");
        assertThat(updated.getPrice()).isEqualTo(2000.0);
        verify(productRepository, times(1)).save(sampleProduct);
    }

    // 6️⃣ Test deleteProduct
    @Test
    void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        doNothing().when(productRepository).delete(sampleProduct);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(sampleProduct);
    }
}
