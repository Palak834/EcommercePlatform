package com.ecommerce.loginservice.repository;

import com.ecommerce.loginservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JPA repository interface for User entity operations
public interface UserRepository extends JpaRepository<User, Long> {
    // Finds a user by email
	User findByEmail(String email);
}