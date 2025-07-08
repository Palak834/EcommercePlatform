package com.ecommerce.profileservice.dto;

// DTO for user profile data
public class UserProfileDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String role;

    // Constructors
    public UserProfileDTO() {}
    public UserProfileDTO(Long userId, String fullName, String email, String address, String phoneNumber, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}