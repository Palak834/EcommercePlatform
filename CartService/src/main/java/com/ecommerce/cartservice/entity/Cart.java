package com.ecommerce.cartservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

// Entity representing a cart item
@Entity
@Table(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long cartId;

    @NotNull(message="User Id is required")
    private Long userId;

    @NotNull(message="Product Id is required")
    private Long productId;

    @NotNull(message="Quantity is required")
    private Integer quantity;

    @NotNull(message="Total price is required")
    private double totalPrice;

    public Cart() {}

    public Cart(Long cartId, Long userId, Long productId, Integer quantity, double totalPrice) {
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