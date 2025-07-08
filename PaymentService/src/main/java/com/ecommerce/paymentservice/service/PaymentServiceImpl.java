package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentDTO;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Service implementation for handling payment logic
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    // Injects the PaymentRepository dependency
    @Autowired
    private PaymentRepository paymentRepository;

    // Injects the RestTemplate dependency for inter-service communication
    @Autowired
    private RestTemplate restTemplate;

    // URL for the Order Service
    private static final String ORDER_SERVICE_URL = "http://localhost:8080/order/";

    // Implements the makePayment method
    @Override
    public PaymentDTO makePayment(Long userId, Long orderId, Payment payment) {
        logger.info("Processing payment for user: {}, order: {}", userId, orderId);

        // Validate order and user
        Map<String, Object> order = fetchOrder(orderId);
        Long orderUserId = ((Number) order.get("userId")).longValue();
        if (!orderUserId.equals(userId)) {
            throw new RuntimeException("Order does not belong to user: " + userId);
        }

        // Validate payment details
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().isEmpty()) {
            throw new RuntimeException("Payment method is required");
        }

        // Set payment details
        payment.setOrderId(orderId);
        payment.setAmount(((Number) order.get("totalAmount")).doubleValue());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("COMPLETED");

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment processed successfully: {}, order status update is pending admin approval", savedPayment.getPaymentId());

        return convertToDTO(savedPayment);
    }

    // Implements the getPaymentsByUserId method
    @Override
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        logger.info("Retrieving payments for user: {}", userId);

        // Fetch all orders for the user
        List<Map<String, Object>> orders = fetchOrdersByUserId(userId);
        List<Long> orderIds = orders.stream()
                .map(order -> ((Number) order.get("orderId")).longValue())
                .collect(Collectors.toList());

        // Fetch payments for the user's orders
        List<Payment> payments = orderIds.stream()
                .flatMap(orderId -> paymentRepository.findByOrderId(orderId).stream())
                .collect(Collectors.toList());

        return payments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Helper method to fetch order details from the Order Service
    private Map<String, Object> fetchOrder(Long orderId) {
        try {
            String url = ORDER_SERVICE_URL + orderId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> order = response.getBody();
            if (order == null) {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }
            return order;
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Order not found with ID: " + orderId);
        } catch (Exception e) {
            logger.error("Unexpected error fetching order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Error fetching order: " + e.getMessage());
        }
    }

    // Helper method to fetch orders by user ID from the Order Service
    private List<Map<String, Object>> fetchOrdersByUserId(Long userId) {
        try {
            String url = ORDER_SERVICE_URL + "user/" + userId;
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            Map[] orders = response.getBody();
            return orders != null ? List.of(orders) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching orders for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching orders: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error fetching orders for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching orders: " + e.getMessage());
        }
    }

    // Helper method to convert a Payment entity to a PaymentDTO
    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus()
        );
    }
}