package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.CartDTO;
import com.ecommerce.cartservice.entity.Cart;
import java.util.List;

// Service interface for cart operations
public interface CartService {
    // Add item to cart
    CartDTO addToCart(Cart cart);
    
    // Get cart items for a user
    List<CartDTO> getCartByUserId(Long userId);
    
    // Update a cart item
    CartDTO updateCart(Long cartId, Cart cart);
    
    // Remove a cart item
    void removeFromCart(Long cartId);
    
    // Clear all cart items for a user
    void clearCart(Long userId);
}