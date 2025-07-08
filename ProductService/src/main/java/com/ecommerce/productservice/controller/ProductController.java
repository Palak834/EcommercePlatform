package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.service.CategoryService;
import com.ecommerce.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

//REST controller for handling product-related HTTP requests
@RestController
public class ProductController {
	// Logger for tracking product-related operations and errors
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Inject ProductService
    @Autowired
    private ProductService productService;

    // Inject CategoryService
    @Autowired
    private CategoryService categoryService;

    // Handles POST requests to add new product
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            logger.info("Received request to add product: {}", product.getName());
            ProductDTO productDTO = productService.addProduct(product);
            return ResponseEntity.ok(productDTO);
        } catch (RuntimeException e) {
            logger.error("Error adding product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }

    // Handles GET requests to retrieve all products
    @GetMapping("/product")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            logger.info("Received request to get all products");
            return ResponseEntity.ok(productService.getAllProducts());
        } catch (RuntimeException e) {
            logger.error("Error retrieving products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Handles GET requests to retrieve a product by ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        try {
            logger.info("Received request to get product with ID: {}", productId);
            return ResponseEntity.ok(productService.getProductById(productId));
        } catch (RuntimeException e) {
            logger.error("Error retrieving product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Handles GET requests to retrieve a product by category ID
    @GetMapping("/product/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@PathVariable Long categoryId) {
        try {
            logger.info("Received request to get products for category ID: {}", categoryId);
            return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
        } catch (RuntimeException e) {
            logger.error("Error retrieving products by category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Handles PUT requests to update a product by ID (ADMIN only)
    @PutMapping("/product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            logger.info("Received request to update product with ID: {}", productId);
            ProductDTO productDTO = productService.updateProduct(productId, product);
            return ResponseEntity.ok(productDTO);
        } catch (RuntimeException e) {
            logger.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Handles DELETE requests to delete a product by ID (ADMIN only)
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            logger.info("Received request to delete product with ID: {}", productId);
            productService.deleteProduct(productId);
            return ResponseEntity.ok(createSuccessResponse("Product deleted successfully"));
        } catch (RuntimeException e) {
            logger.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Handles GET request to get category
    @GetMapping("/product/category")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            logger.info("Received request to get all categories");
            List<CategoryDTO> categories = categoryService.getAllCategories();
            logger.debug("Returning {} categories", categories.size());
            return ResponseEntity.ok(categories); // Return empty list if no categories
        } catch (RuntimeException e) {
            logger.error("Error retrieving categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Handles POST request to add category
    @PostMapping("/product/category")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            logger.info("Received request to create category: {}", category.getName());
            Category createdCategory = categoryService.createCategory(category);
            return ResponseEntity.ok(categoryService.getCategoryById(createdCategory.getCategoryId()));
        } catch (RuntimeException e) {
            logger.error("Error creating category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e.getMessage()));
        }
    }

    // Handles PUT request to update category
    @PutMapping("/product/category/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        try {
            logger.info("Received request to update category with ID: {}", categoryId);
            return ResponseEntity.ok(categoryService.updateCategory(categoryId, category));
        } catch (RuntimeException e) {
            logger.error("Error updating category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Handles DELETE request to delete category
    @DeleteMapping("/product/category/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            logger.info("Received request to delete category with ID: {}", categoryId);
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(createSuccessResponse("Category deleted successfully"));
        } catch (RuntimeException e) {
            logger.error("Error deleting category: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Creates a standardized error response map with the provided error message
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    // Creates a standardized success response map with the provided success message
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}