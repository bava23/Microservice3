package com.example.product_service.mapper;

import com.example.product_service.dto.ProductRequestDTO;
import com.example.product_service.entity.Product;

public class ProductMapper {

    public static Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        return product;
    }
}
