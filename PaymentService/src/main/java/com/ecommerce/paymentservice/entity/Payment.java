package com.ecommerce.paymentservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// JPA Entity representing a Payment
@Entity
// Specifies the table name in the database
@Table(name="payments")
public class Payment {
	// Primary key for the Payment entity, auto-generated
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long paymentId;

    @NotNull(message="Order Id is required")
    private Long orderId;

    private LocalDateTime paymentDate;

    @NotNull(message="Amount is required")
    private double amount;

    @NotBlank(message="Payment method is required")
    private String paymentMethod;

    @NotBlank(message="Payment status is required")
    private String paymentStatus;

    // Default constructor
    public Payment() {}

    // Constructor with fields
    public Payment(Long paymentId, Long orderId, LocalDateTime paymentDate, double amount, String paymentMethod,
            String paymentStatus) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    // getters and setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}