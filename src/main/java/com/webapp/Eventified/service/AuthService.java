package com.webapp.Eventified.service;


import com.webapp.Eventified.dto.user.LoginRequest;
import com.webapp.Eventified.dto.user.LoginResponse;
import com.webapp.Eventified.dto.user.mailing.AccountVerificationEmailContext;
import com.webapp.Eventified.model.SecureTokenEmail;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.AuthRepository;
import com.webapp.Eventified.repository.SecureTokenRepository;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.util.JWTutil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations.
 * Manages user registration, login, and JWT token generation.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTutil jwtutil;
    private final SecureTokenService SecureTokenService;
    private final SecureTokenRepository secureTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Registers a new user in the system with the provided credentials.
     * Validates that both username and email are unique before creating the
     * account.
     * The password is hashed using BCrypt before storage for security.
     *
     * @param username the desired username for the new user account
     * @param email    the email address for the new user account
     * @param password the plain text password which will be hashed before storage
     * @return User the newly created and saved user entity
     * @throws IllegalArgumentException if the email or username is already in use
     */
    public User registerUser(String username, String email, String password) {

        if (authRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already used.");
        }

        if (authRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already used");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(username, email, hashedPassword);
        User savedUser = authRepository.save(user);
 
        try {
            sendRegistrationEmail(savedUser);
        } catch (Exception e) {
        }

        return savedUser;
    }

    private void sendRegistrationEmail(User user) throws Exception {
        SecureTokenEmail secureToken = SecureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureTokenRepository.save(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext().init(user);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseUrl, secureToken.getToken());

        emailService.sendEmail(emailContext);
    }

    public boolean verifyUser(String token) throws Exception {
        SecureTokenEmail secureToken = secureTokenRepository.findByToken(token);

        if (Objects.isNull(secureToken) || !token.equals(secureToken.getToken())
                || secureToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = userRepository.getOne(secureToken.getUser().getId());

        user.setVerified(true);
        userRepository.save(user);

        secureTokenRepository.delete(secureToken);
        return true;
    }

    /**
     * Authenticates a user with the provided login credentials and generates a JWT
     * token.
     * Validates the username exists and verifies the password against the stored
     * hash.
     *
     * @param request the login request containing username and password
     * @return LoginResponse containing the generated JWT token for authenticated
     *         access
     * @throws IllegalArgumentException if the username is not found or password is
     *                                  incorrect
     */
    public LoginResponse login(LoginRequest request) {
        User user = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        if (!user.isVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in");
        }

        String token = jwtutil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }
}
