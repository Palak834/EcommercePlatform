package com.ecommerce.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Entity class representing the Order table in the database
@Entity
// Specifies the table name in the database
@Table(name = "orders")
public class Order {

	// Primary key with auto-generated value
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull(message="Order date is required")
    private LocalDate orderDate;

    @NotNull(message="User id is required")
    private Long userId;

    @NotNull(message="Total amount is required")
    private double totalAmount;

    @NotNull(message = "Order status is required")
    private String orderStatus;

    @NotNull(message="Quantity is required")
    private Integer quantity;

    // Default constructor
    public Order() {}

    // Constructor with fields
    public Order(LocalDate orderDate, Long userId, Double totalAmount, String orderStatus, Integer quantity) {
        this.orderDate = orderDate;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.quantity = quantity;
    }

    // Getters and setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
