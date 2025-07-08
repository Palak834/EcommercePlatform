package com.ecommerce.productservice.dto;

// Data Transfer Object for Category entity to transfer data between layers
public class CategoryDTO {
    private Long categoryId;
    private String name;

    // Getters and Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}