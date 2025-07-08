package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Implementation of the OrderService interface
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    // Autowire the OrderRepository for database operations
    @Autowired
    private OrderRepository orderRepository;

    // Autowire RestTemplate to make calls to other services
    @Autowired
    private RestTemplate restTemplate;

    // URL for the Cart Service
    private static final String CART_SERVICE_URL = "http://localhost:8080/cart/";

    // Method to create an order from the user's cart
    @Override
    public OrderDTO createOrder(Long userId) {
        logger.info("Creating order for user: {}", userId);

        // Fetch cart items for the user
        List<Map<String, Object>> cartItems = fetchCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + userId);
        }

        // Calculate totalAmount and totalQuantity
        double totalAmount = 0.0;
        int totalQuantity = 0;
        for (Map<String, Object> cartItem : cartItems) {
            totalAmount += ((Number) cartItem.get("totalPrice")).doubleValue();
            totalQuantity += ((Number) cartItem.get("quantity")).intValue();
        }

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(totalAmount);
        order.setQuantity(totalQuantity);
        order.setOrderStatus("PENDING");

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully: {}", savedOrder.getOrderId());

        // Clear the user's cart
        clearCart(userId);

        return convertToDTO(savedOrder);
    }

    // Method to get all orders for a specific user
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        logger.info("Retrieving orders for user: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Method to get a specific order by its ID
    @Override
    public OrderDTO getOrderById(Long orderId) {
        logger.info("Retrieving order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        return convertToDTO(order);
    }
    
    // Method to get all orders
    @Override
    public List<OrderDTO> getAllOrders() {
        logger.info("Retrieving all orders");
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Method to update the status of an order
    @Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        logger.info("Updating status for order {} to {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        logger.info("Order status updated successfully: {}", orderId);
        return convertToDTO(updatedOrder);
    }

    // Method to cancel an order
    @Override
    public void cancelOrder(Long orderId) {
        logger.info("Canceling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
        logger.info("Order canceled successfully: {}", orderId);
    }

    // Helper method to fetch cart items for a user from the Cart Service
    private List<Map<String, Object>> fetchCartItems(Long userId) {
        try {
            String url = CART_SERVICE_URL + "user/" + userId;
            // Make GET request to the Cart Service
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            // Get the response body as an array of Maps
            Map[] cartItems = response.getBody();
            return cartItems != null ? Arrays.asList(cartItems) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching cart for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching cart items: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error fetching cart for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching cart items: " + e.getMessage());
        }
    }

    // Helper method to clear the user's cart in the Cart Service
    private void clearCart(Long userId) {
        try {
            String url = CART_SERVICE_URL + "user/" + userId;
            // Make DELETE request to the Cart Service
            restTemplate.delete(url);
            logger.info("Cart cleared for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error clearing cart for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error clearing cart: " + e.getMessage());
        }
    }

    // Helper method to convert an Order entity to an OrderDTO
    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
                order.getOrderId(),
                order.getOrderDate(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getQuantity()
        );
    }
}