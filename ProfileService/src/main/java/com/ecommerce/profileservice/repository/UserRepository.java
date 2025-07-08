package com.ecommerce.profileservice.repository;

import com.ecommerce.profileservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository interface for User entity
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by email
	Optional<User> findByEmail(String email);
}
