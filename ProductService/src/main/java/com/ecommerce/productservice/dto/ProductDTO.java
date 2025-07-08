package com.ecommerce.productservice.dto;

// Data Transfer Object for Product entity to transfer data between layers
public class ProductDTO {
    private Long productId;
    private String name;
    private Double price;
    private String description;
    private CategoryDTO category;

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
}