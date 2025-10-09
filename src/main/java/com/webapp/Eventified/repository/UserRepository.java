package com.webapp.Eventified.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.webapp.Eventified.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    @NonNull
    Optional<User> findById(@NonNull UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
