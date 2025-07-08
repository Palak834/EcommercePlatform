package com.ecommerce.cartservice.repository;

import com.ecommerce.cartservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JPA repository for cart operations
public interface CartRepository extends JpaRepository<Cart, Long> {
	// Find cart items by user ID
	List<Cart> findByUserId(Long userId);
	
	// Find a specific cart item by user ID and product ID
    Cart findByUserIdAndProductId(Long userId, Long productId);
}