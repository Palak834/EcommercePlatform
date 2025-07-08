package com.ecommerce.orderservice;

import com.ecommerce.orderservice.dto.OrderDTO;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceApplicationTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderId(1L);
        order.setUserId(1L);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(20.0);
        order.setQuantity(2);
        order.setOrderStatus("PENDING");
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        Map<String, Object> cartItem = Map.of(
                "totalPrice", 10.0,
                "quantity", 1
        );
        when(restTemplate.getForEntity("http://localhost:8080/cart/user/1", Map[].class))
                .thenReturn(new ResponseEntity<>(new Map[]{cartItem}, HttpStatus.OK));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(restTemplate).delete("http://localhost:8080/cart/user/1");

        // Act
        OrderDTO result = orderService.createOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(20.0, result.getTotalAmount());
        assertEquals(2, result.getQuantity());
        assertEquals("PENDING", result.getOrderStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/cart/user/1", Map[].class);
        verify(restTemplate, times(1)).delete("http://localhost:8080/cart/user/1");
    }

    @Test
    void testCreateOrder_EmptyCart() {
        // Arrange
        when(restTemplate.getForEntity("http://localhost:8080/cart/user/1", Map[].class))
                .thenReturn(new ResponseEntity<>(new Map[0], HttpStatus.OK));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L);
        });
        assertEquals("Cart is empty for user: 1", exception.getMessage());
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/cart/user/1", Map[].class);
        verify(orderRepository, never()).save(any(Order.class));
        verify(restTemplate, never()).delete(anyString());
    }

    @Test
    void testCreateOrder_FetchCartError() {
        // Arrange
        when(restTemplate.getForEntity("http://localhost:8080/cart/user/1", Map[].class))
                .thenThrow(new RuntimeException("Cart service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(1L);
        });
        assertTrue(exception.getMessage().contains("Error fetching cart items"));
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/cart/user/1", Map[].class);
        verify(orderRepository, never()).save(any(Order.class));
        verify(restTemplate, never()).delete(anyString());
    }

    @Test
    void testGetOrdersByUserId_Success() {
        // Arrange
        when(orderRepository.findByUserId(1L)).thenReturn(Collections.singletonList(order));

        // Act
        List<OrderDTO> result = orderService.getOrdersByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetOrdersByUserId_Empty() {
        // Arrange
        when(orderRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<OrderDTO> result = orderService.getOrdersByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("PENDING", result.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(1L);
        });
        assertEquals("Order not found with ID: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderDTO result = orderService.updateOrderStatus(1L, "SHIPPED");

        // Assert
        assertNotNull(result);
        assertEquals("SHIPPED", result.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, "SHIPPED");
        });
        assertEquals("Order not found with ID: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.cancelOrder(1L);

        // Assert
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals("CANCELLED", order.getOrderStatus());
    }

    @Test
    void testCancelOrder_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L);
        });
        assertEquals("Order not found with ID: 1", exception.getMessage());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}