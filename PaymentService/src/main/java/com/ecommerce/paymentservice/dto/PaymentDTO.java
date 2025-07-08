package com.ecommerce.paymentservice.dto;

import java.time.LocalDateTime;

// Data Transfer Object for Payment information
public class PaymentDTO {

    private Long paymentId;
    private Long orderId;
    private LocalDateTime paymentDate;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;

    // Default constructor
 	public PaymentDTO() {}

 	// Constructor with all fields
    public PaymentDTO(Long paymentId, Long orderId, LocalDateTime paymentDate, double amount, String paymentMethod, String paymentStatus) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    // Getters and setters
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