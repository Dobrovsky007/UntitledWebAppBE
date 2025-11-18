package com.webapp.Eventified.service;

import org.springframework.stereotype.Service;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import java.security.SecureRandom;

import com.webapp.Eventified.model.SecureTokenEmail;
import com.webapp.Eventified.repository.SecureTokenRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class SecureTokenService {
    
    private final SecureTokenRepository secureTokenRepository;
    private static final SecureRandom DEFAULT_TOKEN_GENERATOR = new SecureRandom();

    @Value("${app.token.validity:2800}")
    private int tokenValidityInSeconds;

    public SecureTokenEmail createSecureToken(){
        byte[] tokenBytes = new byte[32]; // 32 bytes = 256 bits
        DEFAULT_TOKEN_GENERATOR.nextBytes(tokenBytes);
        String tokenValue = Base64.encodeBase64URLSafeString(tokenBytes);
        
        SecureTokenEmail secureToken = new SecureTokenEmail();
        secureToken.setToken(tokenValue);
        secureToken.setCreatedAt(LocalDateTime.now());
        secureToken.setExpiresAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds));
        return secureToken;
    }

    public void saveSecureToken(SecureTokenEmail secureToken){
        secureTokenRepository.save(secureToken);
    }

    public SecureTokenEmail findByToken(String token){
        return secureTokenRepository.findByToken(token);
    }

    public void removeToken(SecureTokenEmail secureToken){
        secureTokenRepository.delete(secureToken);
    }
    
}
