package com.webapp.Eventified.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.webapp.Eventified.model.User;

/**
 * Repository interface for User entity operations.
 * Provides database access methods for user management.
 *
 * @author Eventified Team
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return Optional containing the user if found, empty otherwise
     */
    @NonNull
    Optional<User> findById(@NonNull UUID id);
    
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
