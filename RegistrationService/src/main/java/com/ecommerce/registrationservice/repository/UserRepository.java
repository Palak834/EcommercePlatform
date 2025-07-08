package com.ecommerce.registrationservice.repository;

import com.ecommerce.registrationservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

//JPA repository interface for User entity operations
public interface UserRepository extends JpaRepository<User, Long> {
	// Custom query method to find a user by email
	User findByEmail(String email);
}