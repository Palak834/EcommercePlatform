package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Repository interface to perform CRUD operations on Order entity
public interface OrderRepository extends JpaRepository<Order, Long> {
	// method to find orders by user ID
	List<Order> findByUserId(Long userId);
}