package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderDTO;
import java.util.List;

// Service interface for Order related operations
public interface OrderService {
	// Method to create a new order for a user
    OrderDTO createOrder(Long userId);
    
    // Method to get all orders for a specific user  
    List<OrderDTO> getOrdersByUserId(Long userId);
    
    // Method to get a specific order by its ID
    OrderDTO getOrderById(Long orderId);
    
    // Method to get all orders
    List<OrderDTO> getAllOrders();
    
    // Method to update the status of an order
    OrderDTO updateOrderStatus(Long orderId, String status);
    
    // Method to cancel an order
    void cancelOrder(Long orderId);
}