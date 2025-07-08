package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.entity.Product;

import java.util.List;

public interface ProductService {
	// Creates a new product
    ProductDTO addProduct(Product product);
    
    // Retrieves all products
    List<ProductDTO> getAllProducts();
    
    // Retrieves a product by ID
    ProductDTO getProductById(Long productId);
    
    // Retrieves a product by category ID
    List<ProductDTO> getProductsByCategoryId(Long categoryId);
    
    // Updates an existing product
    ProductDTO updateProduct(Long productId, Product updatedProduct);
    
    // Deletes a product by ID
    void deleteProduct(Long productId);
}