package com.ecommerce.cartservice;

import com.ecommerce.cartservice.dto.CartDTO;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.CartServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceApplicationTests {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1L);
        cart.setUserId(1L);
        cart.setProductId(1L);
        cart.setQuantity(2);
        cart.setTotalPrice(0.0); // Will be set by fetchProductPrice
    }

    @Test
    void testAddToCart_Success() {
        // Arrange
        when(cartRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(null);
        when(restTemplate.getForEntity("http://localhost:8080/product/1", Map.class))
                .thenReturn(new ResponseEntity<>(Map.of("price", 10.0), HttpStatus.OK));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        CartDTO result = cartService.addToCart(cart);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getProductId());
        assertEquals(2, result.getQuantity());
        assertEquals(20.0, result.getTotalPrice());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/product/1", Map.class);
    }

    @Test
    void testAddToCart_ProductAlreadyExists() {
        // Arrange
        Cart existingCart = new Cart();
        existingCart.setUserId(1L);
        existingCart.setProductId(1L);
        existingCart.setQuantity(1);
        when(cartRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(existingCart);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(cart);
        });
        assertEquals("Product already exists in cart", exception.getMessage());
        verify(cartRepository, times(1)).findByUserIdAndProductId(1L, 1L);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    void testAddToCart_InvalidProductPrice() {
        // Arrange
        when(cartRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(null);
        when(restTemplate.getForEntity("http://localhost:8080/product/1", Map.class))
                .thenThrow(new RuntimeException("Product not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(cart);
        });
        assertTrue(exception.getMessage().contains("Error fetching product price"));
        verify(cartRepository, times(1)).findByUserIdAndProductId(1L, 1L);
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/product/1", Map.class);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testGetCartByUserId_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Collections.singletonList(cart));

        // Act
        List<CartDTO> result = cartService.getCartByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetCartByUserId_Empty() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<CartDTO> result = cartService.getCartByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateCart_Success() {
        // Arrange
        Cart updatedCart = new Cart();
        updatedCart.setQuantity(3);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(restTemplate.getForEntity("http://localhost:8080/product/1", Map.class))
                .thenReturn(new ResponseEntity<>(Map.of("price", 10.0), HttpStatus.OK));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        CartDTO result = cartService.updateCart(1L, updatedCart);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCartId());
        assertEquals(3, result.getQuantity());
        assertEquals(30.0, result.getTotalPrice());
        verify(cartRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/product/1", Map.class);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testUpdateCart_NotFound() {
        // Arrange
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCart(1L, cart);
        });
        assertEquals("Cart item not found with ID: 1", exception.getMessage());
        verify(cartRepository, times(1)).findById(1L);
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testRemoveFromCart_Success() {
        // Arrange
        when(cartRepository.existsById(1L)).thenReturn(true);

        // Act
        cartService.removeFromCart(1L);

        // Assert
        verify(cartRepository, times(1)).existsById(1L);
        verify(cartRepository, times(1)).deleteById(1L);
    }

    @Test
    void testRemoveFromCart_NotFound() {
        // Arrange
        when(cartRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.removeFromCart(1L);
        });
        assertEquals("Cart item not found with ID: 1", exception.getMessage());
        verify(cartRepository, times(1)).existsById(1L);
        verify(cartRepository, never()).deleteById(anyLong());
    }

    @Test
    void testClearCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Collections.singletonList(cart));

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartRepository, times(1)).findByUserId(1L);
        verify(cartRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void testClearCart_Empty() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartRepository, times(1)).findByUserId(1L);
        verify(cartRepository, times(1)).deleteAll(anyList());
    }
}