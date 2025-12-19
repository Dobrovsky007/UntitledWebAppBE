package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.dto.user.LoginRequest;
import com.webapp.Eventified.model.SecureTokenEmail;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.AuthRepository;
import com.webapp.Eventified.repository.SecureTokenRepository;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.service.AuthService;
import com.webapp.Eventified.util.JWTutil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * ========================================================================
 * EXAMPLE TEST CLASS WITH BEST PRACTICES
 * ========================================================================
 * 
 * This is a TEACHING EXAMPLE showing you how to write tests properly.
 * Study this file to learn the patterns, then apply them to your own tests!
 * 
 * Copy the 4 new tests at the bottom to your actual AuthServiceTest.java
 */
class AuthServiceTestExample {

    // Dependencies that will be mocked
    private AuthRepository authRepository;
    private SecureTokenRepository secureTokenRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JWTutil jwtutil;
    
    // The service we're testing
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Create mocks for all dependencies
        authRepository = mock(AuthRepository.class);
        secureTokenRepository = mock(SecureTokenRepository.class);
        userRepository = mock(UserRepository.class);
        jwtutil = mock(JWTutil.class);
        
        // Use real password encoder (not mocked because it doesn't touch external systems)
        passwordEncoder = new BCryptPasswordEncoder();
        
        // Create the service with mocked dependencies
        authService = new AuthService(
            authRepository, 
            passwordEncoder, 
            jwtutil, 
            null,  // SecureTokenService - not needed for these tests
            secureTokenRepository, 
            userRepository, 
            null   // EmailService - not needed for these tests
        );
    }

    // ========================================================================
    // EXAMPLE 1: Testing a Success Scenario
    // ========================================================================
    
    @Test
    @DisplayName("Should successfully verify user with valid token")
    void verifyUser_success() throws Exception {
        // === LEARNING POINT 1: Testing the Happy Path ===
        // Always test the success scenario first to understand what SHOULD happen
        
        // ARRANGE: Create test data
        String token = "valid-token-123";
        UUID userId = UUID.randomUUID();
        
        // Create a user who is not verified yet
        User user = new User("testuser", "test@example.com", "hashedpass");
        user.setId(userId);
        user.setVerified(false);  // Important: user starts unverified
        
        // Create a valid token that hasn't expired
        SecureTokenEmail secureToken = new SecureTokenEmail();
        secureToken.setToken(token);
        secureToken.setUser(user);
        secureToken.setExpiresAt(LocalDateTime.now().plusHours(1));  // Token expires in future
        
        // Configure mocks - tell them what to return
        when(secureTokenRepository.findByToken(token)).thenReturn(secureToken);
        when(userRepository.getOne(userId)).thenReturn(user);
        
        // ACT: Call the method we're testing
        boolean result = authService.verifyUser(token);
        
        // ASSERT: Check the results
        assertTrue(result, "Verification should succeed");
        assertTrue(user.isVerified(), "User should be marked as verified");
        
        // === LEARNING POINT 2: Verify Mock Interactions ===
        // This ensures the service actually called the right methods
        verify(secureTokenRepository).findByToken(token);
        verify(userRepository).getOne(userId);
        verify(userRepository).save(user);  // User should be saved with verified=true
        verify(secureTokenRepository).delete(secureToken);  // Token should be deleted after use
    }

    // ========================================================================
    // EXAMPLE 2: Testing a Failure Scenario (Invalid Input)
    // ========================================================================
    
    @Test
    @DisplayName("Should throw exception when token is invalid (not found)")
    void verifyUser_invalidToken_throwsException() {
        // === LEARNING POINT 3: Testing Failure Scenarios ===
        // Test what happens when things go wrong
        
        // ARRANGE
        String invalidToken = "non-existent-token";
        
        // Mock returns null for invalid token (token doesn't exist in database)
        when(secureTokenRepository.findByToken(invalidToken)).thenReturn(null);
        
        // ACT & ASSERT: Use assertThrows to catch expected exceptions
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            authService.verifyUser(invalidToken)
        );
        
        // Verify the exception has the right message
        assertEquals("Invalid or expired token", exception.getMessage());
        
        // === LEARNING POINT 4: Verify What DIDN'T Happen ===
        // Make sure the service didn't save anything when verification failed
        verify(userRepository, never()).save(any(User.class));
        verify(secureTokenRepository, never()).delete(any(SecureTokenEmail.class));
    }

    // ========================================================================
    // EXAMPLE 3: Testing Another Failure Scenario (Expired Token)
    // ========================================================================
    
    @Test
    @DisplayName("Should throw exception when token is expired")
    void verifyUser_expiredToken_throwsException() {
        // ARRANGE
        String token = "expired-token-456";
        UUID userId = UUID.randomUUID();
        
        User user = new User("testuser", "test@example.com", "hashedpass");
        user.setId(userId);
        
        // Create an EXPIRED token (expires in the past)
        SecureTokenEmail secureToken = new SecureTokenEmail();
        secureToken.setToken(token);
        secureToken.setUser(user);
        secureToken.setExpiresAt(LocalDateTime.now().minusHours(1));  // Already expired!
        
        when(secureTokenRepository.findByToken(token)).thenReturn(secureToken);
        
        // ACT & ASSERT
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            authService.verifyUser(token)
        );
        
        assertEquals("Invalid or expired token", exception.getMessage());
        
        // Verify nothing was saved/deleted
        verify(userRepository, never()).save(any(User.class));
        verify(secureTokenRepository, never()).delete(any(SecureTokenEmail.class));
    }

    // ========================================================================
    // EXAMPLE 4: Testing Business Logic Rules
    // ========================================================================
    
    @Test
    @DisplayName("Should throw exception when logging in with unverified account")
    void login_unverifiedUser_throwsException() {
        // === LEARNING POINT 5: Test Business Logic Rules ===
        // This tests that users must verify email before logging in
        
        // ARRANGE
        String username = "testuser";
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);
        
        // Create user who hasn't verified their email
        User user = new User(username, "test@example.com", hashedPassword);
        user.setVerified(false);  // This is the key: user is NOT verified
        
        when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));
        
        LoginRequest request = new LoginRequest(username, password);
        
        // ACT & ASSERT
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            authService.login(request)
        );
        
        assertEquals("Please verify your email before logging in", exception.getMessage());
        
        // === LEARNING POINT 6: Verify Flow Stopped Early ===
        // JWT token should NOT be generated for unverified users
        verify(jwtutil, never()).generateToken(any());
    }
    
    // ========================================================================
    // BONUS: Improved version of existing test
    // ========================================================================
    
    @Test
    @DisplayName("Should successfully register user with valid credentials")
    void registerUser_success() {
        // ARRANGE - Set up test data
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // Configure mock behavior
        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT - Execute the method
        User user = authService.registerUser(username, email, password);

        // ASSERT - Verify results using assertAll for better error messages
        assertAll(
            () -> assertEquals(username, user.getUsername(), "Username should match"),
            () -> assertEquals(email, user.getEmail(), "Email should match"),
            () -> assertTrue(passwordEncoder.matches(password, user.getPasswordHash()), 
                "Password should be hashed correctly")
        );
        
        // VERIFY - Check mock interactions
        verify(authRepository).findByEmail(email);
        verify(authRepository).findByUsername(username);
        verify(authRepository).save(any(User.class));
    }
}
