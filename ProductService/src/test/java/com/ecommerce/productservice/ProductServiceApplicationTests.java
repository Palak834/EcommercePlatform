package com.ecommerce.productservice;

import com.ecommerce.productservice.dto.CategoryDTO;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.CategoryService;
import com.ecommerce.productservice.service.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceApplicationTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        // Clear SecurityContext to avoid interference from previous tests
        SecurityContextHolder.clearContext();

        category = new Category();
        category.setCategoryId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setProductId(1L);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setDescription("A high-performance laptop for professionals");
        product.setCategory(category);
    }

    @Test
    void testAddProduct_Success() {
        // Arrange
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDTO result = productService.addProduct(product);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        assertEquals("A high-performance laptop for professionals", result.getDescription());
        assertEquals(1L, result.getCategory().getCategoryId());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void testAddProduct_InvalidName() {
        // Arrange
        product.setName(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(product);
        });
        assertEquals("Product name is required", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(categoryService, never()).createCategory(any(Category.class));
    }

    @Test
    void testAddProduct_InvalidPrice() {
        // Arrange
        product.setPrice(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(product);
        });
        assertEquals("Valid product price is required", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(categoryService, never()).createCategory(any(Category.class));
    }

    @Test
    void testAddProduct_InvalidDescription() {
        // Arrange
        product.setDescription(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(product);
        });
        assertEquals("Product description is required", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(categoryService, never()).createCategory(any(Category.class));
    }

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        // Act
        List<ProductDTO> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("A high-performance laptop for professionals", result.get(0).getDescription());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("A high-performance laptop for professionals", result.getDescription());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(1L);
        });
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductsByCategoryId_Success() {
        // Arrange
        when(productRepository.findByCategory_CategoryId(1L)).thenReturn(Collections.singletonList(product));

        // Act
        List<ProductDTO> result = productService.getProductsByCategoryId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("A high-performance laptop for professionals", result.get(0).getDescription());
        verify(productRepository, times(1)).findByCategory_CategoryId(1L);
    }

    @Test
    void testGetProductsByCategoryId_NotFound() {
        // Arrange
        when(productRepository.findByCategory_CategoryId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductsByCategoryId(1L);
        });
        assertEquals("No products found for category ID: 1", exception.getMessage());
        verify(productRepository, times(1)).findByCategory_CategoryId(1L);
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setPrice(1099.99);
        updatedProduct.setDescription("An updated high-performance laptop");
        updatedProduct.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductDTO result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Laptop", result.getName());
        assertEquals(1099.99, result.getPrice());
        assertEquals("An updated high-performance laptop", result.getDescription());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(1L, product);
        });
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(1L);
        });
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}