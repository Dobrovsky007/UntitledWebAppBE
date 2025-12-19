package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.dto.user.LoginRequest;
import com.webapp.Eventified.dto.user.LoginResponse;
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

import java.util.Optional;

class AuthServiceTest {

    private AuthRepository authRepository;
    private SecureTokenRepository secureTokenRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JWTutil jwtutil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authRepository = mock(AuthRepository.class);
        secureTokenRepository = mock(SecureTokenRepository.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtutil = mock(JWTutil.class);
        authService = new AuthService(authRepository, passwordEncoder, jwtutil, null, secureTokenRepository, userRepository, null);
    }

    @Test
    @DisplayName("Should successfully register user with valid credentials")
    void registerUser_success() {
        // STEP 1: ARRANGE - Set up test data
        // Create the input values we'll use for testing
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // STEP 2: ARRANGE - Configure mock behavior
        // Tell the mocks what to return when methods are called
        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // STEP 3: ACT - Execute the method we're testing
        User user = authService.registerUser(username, email, password);

        // STEP 4: ASSERT - Verify the results
        // Use assertAll to group related assertions - if one fails, you see all failures
        assertAll(
            () -> assertEquals(username, user.getUsername(), "Username should match input"),
            () -> assertEquals(email, user.getEmail(), "Email should match input"),
            () -> assertTrue(passwordEncoder.matches(password, user.getPasswordHash()), 
                "Password should be hashed correctly")
        );
        
        // STEP 5: VERIFY - Check that mocks were called correctly
        // This ensures the service actually interacted with dependencies
        verify(authRepository).findByEmail(email);
        verify(authRepository).findByUsername(username);
        verify(authRepository).save(any(User.class));
    }

    @Test
    void registerUser_emailAlreadyUsed_throwsException() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.registerUser(username, email, password)
        );
        assertEquals("Email is already used.", exception.getMessage());
    }

    @Test
    void registerUser_usernameAlreadyUsed_throwsException() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.registerUser(username, email, password)
        );
        assertEquals("Username is already used", exception.getMessage());
    }

    @Test
    void login_success() {
        String username = "testuser";
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, "test@example.com", hashedPassword);

        when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtutil.generateToken(username)).thenReturn("mocked-jwt-token");

        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = authService.login(request);

        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void login_usernameNotFound_throwsException() {
        String username = "unknown";
        String password = "password123";
        when(authRepository.findByUsername(username)).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest(username, password);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(request)
        );
        assertEquals("Username not found", exception.getMessage());
    }

    @Test
    void login_invalidPassword_throwsException() {
        String username = "testuser";
        String password = "wrongpassword";
        String correctPassword = "password123";
        String hashedPassword = passwordEncoder.encode(correctPassword);
        User user = new User(username, "test@example.com", hashedPassword);

        when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(username, password);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(request)
        );
        assertEquals("Invalid credentials", exception.getMessage());
    }
}