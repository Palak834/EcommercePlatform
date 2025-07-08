package com.ecommerce.orderservice.dto;

import java.time.LocalDate;

// Data Transfer Object for Order information
public class OrderDTO {
    private Long orderId;
    private LocalDate orderDate;
    private Long userId;
    private Double totalAmount;
    private String orderStatus;
    private Integer quantity;

    // Default constructor
    public OrderDTO() {}

    // Parameterized constructor
    public OrderDTO(Long orderId, LocalDate orderDate, Long userId, Double totalAmount, String orderStatus, Integer quantity) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.quantity = quantity;
    }

    // Getters and setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    // Override toString method for easy logging and debugging
    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}