package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
	// Returns a list of products associated with the specified category ID
	List<Product> findByCategory_CategoryId(Long categoryId);
}