package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.CartDTO;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST controller for cart-related operations
@RestController
@RequestMapping("/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    // Handle POST request to add a product to the cart
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Cart cart) {
        try {
            logger.info("Received request to add product {} to cart for user {}", 
                    cart.getProductId(), cart.getUserId());
            CartDTO cartDTO = cartService.addToCart(cart);
            return ResponseEntity.ok(cartDTO);
        } catch (RuntimeException e) {
            logger.error("Error adding to cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Handle GET request to retrieve cart items for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartDTO>> getCartByUserId(@PathVariable Long userId) {
        try {
            logger.info("Received request to get cart for user: {}", userId);
            return ResponseEntity.ok(cartService.getCartByUserId(userId));
        } catch (RuntimeException e) {
            logger.error("Error retrieving cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Handle PUT request to update a cart item
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCart(@PathVariable Long cartId, @RequestBody Cart cart) {
        try {
            logger.info("Received request to update cart item: {}", cartId);
            CartDTO cartDTO = cartService.updateCart(cartId, cart);
            return ResponseEntity.ok(cartDTO);
        } catch (RuntimeException e) {
            logger.error("Error updating cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Handle DELETE request to remove a cart item
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId) {
        try {
            logger.info("Received request to remove cart item: {}", cartId);
            cartService.removeFromCart(cartId);
            return ResponseEntity.ok("Cart item removed successfully");
        } catch (RuntimeException e) {
            logger.error("Error removing from cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Handle DELETE request to clear all cart items for a user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        try {
            logger.info("Received request to clear cart for user: {}", userId);
            cartService.clearCart(userId);
            return ResponseEntity.ok("Cart cleared successfully");
        } catch (RuntimeException e) {
            logger.error("Error clearing cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}