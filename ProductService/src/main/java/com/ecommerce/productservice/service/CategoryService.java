package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.entity.Category;

import java.util.List;

public interface CategoryService {
	// Creates a new category and persists it to the database
    Category createCategory(Category category);

    // Retrieves all categories from the database, returning them as DTOs
    List<CategoryDTO> getAllCategories();

    // Retrieves a category by its ID, returning it as a DTO
    CategoryDTO getCategoryById(Long categoryId);

    // Updates an existing category with new details, returning the updated category as a DTO
    CategoryDTO updateCategory(Long categoryId, Category updatedCategory);

    // Deletes a category by its ID from the database
    void deleteCategory(Long categoryId);
}