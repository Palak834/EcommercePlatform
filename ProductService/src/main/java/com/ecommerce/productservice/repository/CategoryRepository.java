package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	// Finds a Category by its name, returning an Optional to handle cases where no category is found
	Optional<Category> findByName(String name);
}