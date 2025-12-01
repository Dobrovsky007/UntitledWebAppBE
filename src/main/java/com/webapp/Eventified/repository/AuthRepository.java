package com.webapp.Eventified.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for authentication-related User operations.
 * Provides database access methods for user authentication and registration.
 *
 * @author Eventified Team
 * @version 1.0
 */
public interface AuthRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
}
