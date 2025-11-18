package com.webapp.Eventified.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webapp.Eventified.model.SecureTokenEmail;

public interface SecureTokenRepository extends JpaRepository<SecureTokenEmail, UUID> {

    SecureTokenEmail findByToken(String token);
    UUID deleteByToken(String token);
    
}
