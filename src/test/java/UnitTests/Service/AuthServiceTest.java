package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.dto.user.mailing.AbstractEmailContext;
import com.webapp.Eventified.dto.user.LoginRequest;
import com.webapp.Eventified.dto.user.LoginResponse;
import com.webapp.Eventified.model.SecureTokenEmail;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.AuthRepository;
import com.webapp.Eventified.repository.SecureTokenRepository;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.service.AuthService;
import com.webapp.Eventified.service.EmailService;
import com.webapp.Eventified.service.SecureTokenService;
import com.webapp.Eventified.util.JWTutil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class AuthServiceTest {

    private AuthRepository authRepository;
    private SecureTokenRepository secureTokenRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JWTutil jwtutil;
    private SecureTokenService secureTokenService;
    private EmailService emailService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authRepository = mock(AuthRepository.class);
        secureTokenRepository = mock(SecureTokenRepository.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtutil = mock(JWTutil.class);
        secureTokenService = mock(SecureTokenService.class);
        emailService = mock(EmailService.class);

        authService = new AuthService(authRepository, passwordEncoder, jwtutil, secureTokenService, secureTokenRepository, userRepository, emailService);
    }

    @Test
    @DisplayName("Should successfully register user with valid credentials")
    void registerUser_success() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // AuthService relies on a Spring-injected @Value for baseUrl.
        // In pure unit tests we set it explicitly to keep behavior deterministic.
        ReflectionTestUtils.setField(authService, "baseUrl", "http://localhost:8080");

        SecureTokenEmail generatedToken = new SecureTokenEmail();
        generatedToken.setToken("test-token");
        generatedToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(secureTokenService.createSecureToken()).thenReturn(generatedToken);
        when(secureTokenRepository.save(any(SecureTokenEmail.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendEmail(any(AbstractEmailContext.class));

        // Act
        User user = authService.registerUser(username, email, password);

        // Assert
        assertAll(
            () -> assertEquals(username, user.getUsername(), "Username should match input"),
            () -> assertEquals(email, user.getEmail(), "Email should match input"),
            () -> assertTrue(passwordEncoder.matches(password, user.getPasswordHash()), 
                "Password should be hashed correctly")
        );

        ArgumentCaptor<SecureTokenEmail> secureTokenCaptor = ArgumentCaptor.forClass(SecureTokenEmail.class);
        verify(secureTokenRepository).save(secureTokenCaptor.capture());
        assertSame(user, secureTokenCaptor.getValue().getUser(), "Secure token should be associated to saved user");
        assertEquals("test-token", secureTokenCaptor.getValue().getToken(), "Secure token value should be used");

        verify(emailService).sendEmail(any(AbstractEmailContext.class));
        
        // Verify
        verify(authRepository).findByEmail(email);
        verify(authRepository).findByUsername(username);
        verify(authRepository).save(any(User.class));
        verify(secureTokenService).createSecureToken();
    }

    @Test
    @DisplayName("registerUser: email already used throws")
    void registerUser_emailAlreadyUsed_throwsException() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.registerUser(username, email, password)
        );

        // Assert
        assertEquals("Email is already used.", exception.getMessage());

        // Verify
        verify(authRepository).findByEmail(email);
        verify(authRepository, never()).save(any());
        verifyNoInteractions(secureTokenService);
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("registerUser: username already used throws")
    void registerUser_usernameAlreadyUsed_throwsException() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.registerUser(username, email, password)
        );

        // Assert
        assertEquals("Username is already used", exception.getMessage());

        // Verify
        verify(authRepository).findByEmail(email);
        verify(authRepository).findByUsername(username);
        verify(authRepository, never()).save(any());
        verifyNoInteractions(secureTokenService);
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("login: success")
    void login_success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, "test@example.com", hashedPassword);
        user.setVerified(true);

        when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtutil.generateToken(username)).thenReturn("mocked-jwt-token");

        LoginRequest request = new LoginRequest(username, password);

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertEquals("mocked-jwt-token", response.getToken());

        // Verify
        verify(authRepository).findByUsername(username);
        verify(jwtutil).generateToken(username);
    }

    @Test
    @DisplayName("login: username not found throws")
    void login_usernameNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        String password = "password123";
        when(authRepository.findByUsername(username)).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest(username, password);

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(request)
        );

        // Assert
        assertEquals("Invalid username or password", exception.getMessage());

        // Verify
        verify(authRepository).findByUsername(username);
        verify(jwtutil, never()).generateToken(any());
    }

    @Test
    @DisplayName("login: invalid password throws")
    void login_invalidPassword_throwsException() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        String correctPassword = "password123";
        String hashedPassword = passwordEncoder.encode(correctPassword);
        User user = new User(username, "test@example.com", hashedPassword);

        when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(username, password);

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(request)
        );

        // Assert
        assertEquals("Invalid username or password", exception.getMessage());

        // Verify
        verify(authRepository).findByUsername(username);
        verify(jwtutil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should successfully verify user with valid token")
    void verifyUser_success() throws Exception {
        // Arrange
        String token = "valid-token-123";
        UUID userId = UUID.randomUUID();

        User user = new User("testuser", "test@example.com", "hashedpass");
        user.setId(userId);
        user.setVerified(false);

        SecureTokenEmail secureToken = new SecureTokenEmail();
        secureToken.setToken(token);
        secureToken.setUser(user);
        secureToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(secureTokenRepository.findByToken(token)).thenReturn(secureToken);
        when(userRepository.getOne(userId)).thenReturn(user);

        // Act
        boolean result = authService.verifyUser(token);

        // Assert
        assertTrue(result);
        assertTrue(user.isVerified());

        // Verify
        verify(secureTokenRepository).findByToken(token);
        verify(userRepository).getOne(userId);
        verify(userRepository).save(user);
        verify(secureTokenRepository).delete(secureToken);
    }

    @Test
    @DisplayName("Should throw exception when token is invalid (not found)")
    void verifyUser_invalidToken_throwsException() {
        // Arrange
        String invalidToken = "non-existent-token";

        when(secureTokenRepository.findByToken(invalidToken)).thenReturn(null);

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.verifyUser(invalidToken)
        );

        // Assert
        assertEquals("Invalid or expired token", exception.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
        verify(secureTokenRepository, never()).delete(any(SecureTokenEmail.class));
    }

    @Test
    @DisplayName("Should throw exception when token is expired")
    void verifyUser_expiredToken_throwsException() {
        // Arrange
        String token = "expired-token-456";
        UUID userId = UUID.randomUUID();

        User user = new User("testuser", "test@example.com", "hashedpass");
        user.setId(userId);

        SecureTokenEmail secureToken = new SecureTokenEmail();
        secureToken.setToken(token);
        secureToken.setUser(user);
        secureToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(secureTokenRepository.findByToken(token)).thenReturn(secureToken);

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.verifyUser(token)
        );

        // Assert
        assertEquals("Invalid or expired token", exception.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
        verify(secureTokenRepository, never()).delete(any(SecureTokenEmail.class));
    }

    @Test
    @DisplayName("login: unverified user throws")
    void login_unverifiedUser_throwsException() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(username, "test@example.com", hashedPassword);
        user.setVerified(false);

        when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(username, password);

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(request)
        );

        // Assert
        assertEquals("Please verify your email before logging in", exception.getMessage());

        // Verify
        verify(jwtutil, never()).generateToken(any());
    }
}