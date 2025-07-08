package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.CartDTO;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Implementation of CartService
@Service
public class CartServiceImpl implements CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;

    // URL for ProductService
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8080/product/";

    @Override
    public CartDTO addToCart(Cart cart) {
        logger.info("Adding product {} to cart for user {}", cart.getProductId(), cart.getUserId());

        // Check if product already exists in cart
        Cart existingCartItem = cartRepository.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
        if (existingCartItem != null) {
            throw new RuntimeException("Product already exists in cart");
        }

        // Set default quantity if not provided
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            cart.setQuantity(1);
        }

        // Fetch product price from ProductService
        double productPrice = fetchProductPrice(cart.getProductId());
        cart.setTotalPrice(productPrice * cart.getQuantity());

        Cart savedCart = cartRepository.save(cart);
        logger.info("Product added to cart successfully: {}", savedCart.getCartId());
        return convertToDTO(savedCart);
    }

    @Override
    public List<CartDTO> getCartByUserId(Long userId) {
        logger.info("Retrieving cart for user: {}", userId);
        // Fetch cart items for user
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public CartDTO updateCart(Long cartId, Cart cart) {
        logger.info("Updating cart item: {}", cartId);
        // Find existing cart item
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartId));

        // Update productId if provided
        if (cart.getProductId() != null) {
            existingCart.setProductId(cart.getProductId());
        }

        // Update quantity if provided, otherwise keep existing
        if (cart.getQuantity() != null && cart.getQuantity() > 0) {
            existingCart.setQuantity(cart.getQuantity());
        }

        // Fetch product price and recalculate totalPrice
        double productPrice = fetchProductPrice(existingCart.getProductId());
        existingCart.setTotalPrice(productPrice * existingCart.getQuantity());

        // Save updated cart item
        Cart updatedCart = cartRepository.save(existingCart);
        logger.info("Cart item updated successfully: {}", cartId);
        return convertToDTO(updatedCart);
    }

    @Override
    public void removeFromCart(Long cartId) {
        logger.info("Removing cart item: {}", cartId);
        // Check if cart item exists
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Cart item not found with ID: " + cartId);
        }
        // Delete cart item
        cartRepository.deleteById(cartId);
        logger.info("Cart item removed successfully: {}", cartId);
    }

    @Override
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user: {}", userId);
        // Fetch user's cart items
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        // Delete all cart items
        cartRepository.deleteAll(cartItems);
        logger.info("Cart cleared successfully for user: {}", userId);
    }

    // Fetch product price from ProductService
    private double fetchProductPrice(Long productId) {
        try {
            String url = PRODUCT_SERVICE_URL + productId;
            // Make HTTP request to ProductService
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> product = response.getBody();
            if (product != null && product.containsKey("price")) {
                return ((Number) product.get("price")).doubleValue();
            } else {
                throw new RuntimeException("Price not found in product response");
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Product not found with ID: " + productId);
        } catch (Exception e) {
            logger.error("Unexpected error fetching product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Error fetching product price: " + e.getMessage());
        }
    }

    // Convert Cart entity to CartDTO
    private CartDTO convertToDTO(Cart cart) {
        return new CartDTO(
                cart.getCartId(),
                cart.getUserId(),
                cart.getProductId(),
                cart.getQuantity(),
                cart.getTotalPrice()
        );
    }
}