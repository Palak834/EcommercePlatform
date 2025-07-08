package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Inject ProductRepository for database operations
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    // Creates a new product and saves it to the database
    @Override
    public ProductDTO addProduct(Product product) {
        logger.info("Adding product: {}", product.getName());
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new RuntimeException("Valid product price is required");
        }
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Product description is required");
        }
        if (product.getCategory() == null || product.getCategory().getName() == null || product.getCategory().getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }

        Category category = categoryService.createCategory(product.getCategory());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    // Retrieves all products from the database
    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info("Retrieving all products with categories");
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Retrieves a product by ID from the database
    @Override
    public ProductDTO getProductById(Long productId) {
        logger.info("Retrieving product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        return convertToDTO(product);
    }

    // Retrieves a product by category ID
    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        logger.info("Retrieving products for category ID: {}", categoryId);
        List<Product> products = productRepository.findByCategory_CategoryId(categoryId);
        if (products.isEmpty()) {
            throw new RuntimeException("No products found for category ID: " + categoryId);
        }
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Updates an existing product in the database
    @Override
    public ProductDTO updateProduct(Long productId, Product updatedProduct) {
        logger.info("Updating product with ID: {}", productId);
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        if (updatedProduct.getName() != null && !updatedProduct.getName().trim().isEmpty()) {
            existingProduct.setName(updatedProduct.getName());
        }
        if (updatedProduct.getPrice() != null && updatedProduct.getPrice() > 0) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getDescription() != null && !updatedProduct.getDescription().trim().isEmpty()) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getName() != null && !updatedProduct.getCategory().getName().trim().isEmpty()) {
            Category category = categoryService.createCategory(updatedProduct.getCategory());
            existingProduct.setCategory(category);
        }
        Product savedProduct = productRepository.save(existingProduct);
        return convertToDTO(savedProduct);
    }

    // Deletes a product from the database by ID
    @Override
    public void deleteProduct(Long productId) {
        logger.info("Deleting product with ID: {}", productId);
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    // Converts ProductDTO to Product entity
    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());

        Category category = product.getCategory();
        if (category != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryId(category.getCategoryId());
            categoryDTO.setName(category.getName());
            productDTO.setCategory(categoryDTO);
        }

        return productDTO;
    }
}