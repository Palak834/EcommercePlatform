package com.ecommerce.paymentservice;

import com.ecommerce.paymentservice.dto.PaymentDTO;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.service.PaymentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceApplicationTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private Map<String, Object> order;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setOrderId(1L);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(100.0);
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setPaymentStatus("COMPLETED");

        order = Map.of(
                "orderId", 1,
                "userId", 1,
                "totalAmount", 100.0
        );
    }

    @Test
    void testMakePayment_Success() {
        // Arrange
        when(restTemplate.getForEntity("http://localhost:8080/order/1", Map.class))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentDTO result = paymentService.makePayment(1L, 1L, payment);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(100.0, result.getAmount());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getPaymentStatus());
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/1", Map.class);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testMakePayment_OrderNotBelongingToUser() {
        // Arrange
        Map<String, Object> differentUserOrder = Map.of(
                "orderId", 1,
                "userId", 2, // Different user
                "totalAmount", 100.0
        );
        when(restTemplate.getForEntity("http://localhost:8080/order/1", Map.class))
                .thenReturn(new ResponseEntity<>(differentUserOrder, HttpStatus.OK));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.makePayment(1L, 1L, payment);
        });
        assertEquals("Order does not belong to user: 1", exception.getMessage());
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/1", Map.class);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testMakePayment_MissingPaymentMethod() {
        // Arrange
        payment.setPaymentMethod(null);
        when(restTemplate.getForEntity("http://localhost:8080/order/1", Map.class))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.makePayment(1L, 1L, payment);
        });
        assertEquals("Payment method is required", exception.getMessage());
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/1", Map.class);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testMakePayment_OrderNotFound() {
        // Arrange
        when(restTemplate.getForEntity("http://localhost:8080/order/1", Map.class))
                .thenThrow(new RuntimeException("Order not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.makePayment(1L, 1L, payment);
        });
        assertTrue(exception.getMessage().contains("Order not found"));
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/1", Map.class);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testGetPaymentsByUserId_Success() {
        // Arrange
        Map<String, Object> userOrder = Map.of(
                "orderId", 1,
                "userId", 1
        );
        when(restTemplate.getForEntity("http://localhost:8080/order/user/1", Map[].class))
                .thenReturn(new ResponseEntity<>(new Map[]{userOrder}, HttpStatus.OK));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Collections.singletonList(payment));

        // Act
        List<PaymentDTO> result = paymentService.getPaymentsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(100.0, result.get(0).getAmount());
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/user/1", Map[].class);
        verify(paymentRepository, times(1)).findByOrderId(1L);
    }

    @Test
    void testGetPaymentsByUserId_NoOrders() {
        // Arrange
        when(restTemplate.getForEntity("http://localhost:8080/order/user/1", Map[].class))
                .thenReturn(new ResponseEntity<>(new Map[0], HttpStatus.OK));

        // Act
        List<PaymentDTO> result = paymentService.getPaymentsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/user/1", Map[].class);
        verify(paymentRepository, never()).findByOrderId(anyLong());
    }

    @Test
    void testGetPaymentsByUserId_FetchOrdersError() {
        // Arrange
        when(restTemplate.getForEntity("http://localhost:8080/order/user/1", Map[].class))
                .thenThrow(new RuntimeException("Order service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentsByUserId(1L);
        });
        assertTrue(exception.getMessage().contains("Error fetching orders"));
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/order/user/1", Map[].class);
        verify(paymentRepository, never()).findByOrderId(anyLong());
    }
}