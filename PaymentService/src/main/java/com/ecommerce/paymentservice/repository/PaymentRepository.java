package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data JPA repository for the Payment entity
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // method to find payments by order ID
	List<Payment> findByOrderId(Long orderId);
}