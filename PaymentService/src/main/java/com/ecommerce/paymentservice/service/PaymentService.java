package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentDTO;
import com.ecommerce.paymentservice.entity.Payment;

import java.util.List;

// Interface for the Payment Service
public interface PaymentService {
	// Method to process a payment for a user and order
    PaymentDTO makePayment(Long userId, Long orderId, Payment payment);
    
    // Method to get all payments for a specific user
    List<PaymentDTO> getPaymentsByUserId(Long userId);
}