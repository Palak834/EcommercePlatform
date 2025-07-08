package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST controller to handle HTTP requests for OrderService
@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    // Create a new order
    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderDTO> createOrder(@PathVariable Long userId) {
        try {
            logger.info("Received request to create order for user: {}", userId);
            OrderDTO orderDTO = orderService.createOrder(userId);
            logger.debug("Returning OrderDTO: {}", orderDTO);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(orderDTO);
        } catch (RuntimeException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new OrderDTO(null, null, userId, null, null, null)); // Return empty DTO with error context
        }
    }

    // Get order by user id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        try {
            logger.info("Received request to get orders for user: {}", userId);
            List<OrderDTO> orderDTOs = orderService.getOrdersByUserId(userId);
            logger.debug("Returning OrderDTOs: {}", orderDTOs);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(orderDTOs.isEmpty() ? List.of() : orderDTOs); // Ensure empty list is returned explicitly
        } catch (RuntimeException e) {
            logger.error("Error retrieving orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }

    // Get order by order id
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            logger.info("Received request to get order: {}", orderId);
            OrderDTO orderDTO = orderService.getOrderById(orderId);
            logger.debug("Returning OrderDTO: {}", orderDTO);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(orderDTO);
        } catch (RuntimeException e) {
            logger.error("Error retrieving order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }
    
    // Get all orders
    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        logger.info("Received request to get all orders");
        List<OrderDTO> orderDTOs = orderService.getAllOrders();
        logger.debug("Returning OrderDTOs: {}", orderDTOs);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderDTOs);
    }

    // Update an order
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody String status) {
        try {
            logger.info("Received request to update status for order: {} to {}", orderId, status);
            OrderDTO orderDTO = orderService.updateOrderStatus(orderId, status);
            logger.debug("Returning OrderDTO: {}", orderDTO);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(orderDTO);
        } catch (RuntimeException e) {
            logger.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }

    // Delete an order by ID
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            logger.info("Received request to cancel order: {}", orderId);
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Order canceled successfully");
        } catch (RuntimeException e) {
            logger.error("Error canceling order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getMessage());
        }
    }
}