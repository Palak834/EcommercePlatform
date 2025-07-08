package com.ecommerce.cartservice.dto;

// Data Transfer Object for Cart
public class CartDTO {
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private double totalPrice;

    // default constructor
    public CartDTO() {}

    // Constructor with fields
    public CartDTO(Long cartId, Long userId, Long productId, Integer quantity, double totalPrice) {
        this.cartId = cartId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}