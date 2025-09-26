package com.webapp.Eventified.service;

import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.LoginRequest;
import com.webapp.Eventified.dto.LoginResponse;
import com.webapp.Eventified.repository.AuthRepository;
import com.webapp.Eventified.util.JWTutil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTutil jwtutil;

    /**
     * Registers a new user in the system with the provided credentials.
     * Validates that both username and email are unique before creating the account.
     * The password is hashed using BCrypt before storage for security.
     *
     * @param username the desired username for the new user account
     * @param email the email address for the new user account
     * @param password the plain text password which will be hashed before storage
     * @return User the newly created and saved user entity
     * @throws IllegalArgumentException if the email or username is already in use
     */
    public User registerUser( String username, String email, String password) {

        if (authRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("Email is already used.");
        }

        if (authRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("Username is already used");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(username, email, hashedPassword);
        User savedUser = authRepository.save(user);
        return savedUser;
    }

    /**
     * Authenticates a user with the provided login credentials and generates a JWT token.
     * Validates the username exists and verifies the password against the stored hash.
     *
     * @param request the login request containing username and password
     * @return LoginResponse containing the generated JWT token for authenticated access
     * @throws IllegalArgumentException if the username is not found or password is incorrect
     */
    public LoginResponse login(LoginRequest request){
        User user = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Username not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtutil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }
}
