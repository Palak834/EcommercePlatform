package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//Implementation of CategoryService
@Service
//Ensures all methods run within a transaction for data consistency
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    // Inject CategoryRepository for database operations
    @Autowired
    private CategoryRepository categoryRepository;

    // Creates a new category, ensuring it doesn't already exist
    @Override
    public Category createCategory(Category category) {
        logger.info("Creating category: {}", category.getName());
        // Validate category name
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }
        // Check for existing category with the same name
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            logger.info("Category already exists: {}", category.getName());
            return existingCategory.get();
        }
        // return the new category
        return categoryRepository.save(category);
    }

    // Retrieves all categories from the database as DTOs
    @Override
    public List<CategoryDTO> getAllCategories() {
        logger.info("Retrieving all categories");
        // Fetch all categories from the repository
        List<Category> categories = categoryRepository.findAll();
        logger.debug("Found {} categories in database", categories.size());
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Retrieves a category by ID, throwing an exception if not found
    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        logger.info("Retrieving category with ID: {}", categoryId);
        // Fetch category by ID or throw exception if not found
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        logger.debug("Retrieved category: {}", category.getName());
        return convertToDTO(category);
    }

    // Updates an existing category with new details
    @Override
    public CategoryDTO updateCategory(Long categoryId, Category updatedCategory) {
        logger.info("Updating category with ID: {}", categoryId);
        // Fetch existing category or throw exception if not found
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        // Validate updated category name
        if (updatedCategory.getName() == null || updatedCategory.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }
        // Update category name and save
        existingCategory.setName(updatedCategory.getName());
        categoryRepository.save(existingCategory);
        logger.debug("Updated category to: {}", existingCategory.getName());
        return convertToDTO(existingCategory);
    }

    // Deletes a category by ID, ensuring it has no associated products
    @Override
    public void deleteCategory(Long categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        // Fetch category or throw exception if not found
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        // Prevent deletion if category has associated products
        if (!category.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot delete category with associated products");
        }
        // Delete the category
        categoryRepository.deleteById(categoryId);
        logger.debug("Deleted category with ID: {}", categoryId);
    }

    // Converts a Category entity to a CategoryDTO for data transfer
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }
}