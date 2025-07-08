package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentDTO;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller for handling payment-related requests
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    // Injects the PaymentService dependency
    @Autowired
    private PaymentService paymentService;

    // Endpoint for making a payment for a specific user and order
    @PostMapping("/user/{userId}/order/{orderId}")
    public ResponseEntity<PaymentDTO> makePayment(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody Payment payment) {
        try {
            logger.info("Received request to make payment for user: {}, order: {}", userId, orderId);
            PaymentDTO paymentDTO = paymentService.makePayment(userId, orderId, payment);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentDTO);
        } catch (RuntimeException e) {
            logger.error("Error processing payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }

    // Endpoint for getting all payments for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable Long userId) {
        try {
            logger.info("Received request to get payments for user: {}", userId);
            List<PaymentDTO> paymentDTOs = paymentService.getPaymentsByUserId(userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentDTOs.isEmpty() ? List.of() : paymentDTOs);
        } catch (RuntimeException e) {
            logger.error("Error retrieving payments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }
}