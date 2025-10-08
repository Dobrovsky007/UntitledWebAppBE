package com.webapp.Eventified.controller.user;

import com.webapp.Eventified.dto.LoginRequest;
import com.webapp.Eventified.dto.RegisterRequest;
import com.webapp.Eventified.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication-related endpoints.
 * Handles user registration and login operations.
 *
 * @author Eventified Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Constructs a new AuthController with the specified AuthService.
     *
     * @param authService the service layer for authentication-related operations
     */
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    /**
     * Registers a new user in the system.
     * Validates that the username and email are unique before creating the account.
     *
     * @param request the registration request containing username, email, and password
     * @return ResponseEntity with HTTP 201 (Created) and success message if registration succeeds,
     *         or HTTP 400 (Bad Request) with error message if validation fails
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request){
        try {
            authService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Authenticates a user and generates a JWT token for subsequent requests.
     * Validates the user's credentials against the stored password hash.
     *
     * @param request the login request containing username and password
     * @return ResponseEntity containing the JWT token if authentication succeeds,
     *         or an error response if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
