package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;

import com.webapp.Eventified.dto.user.LoginRequest;
import com.webapp.Eventified.dto.user.LoginResponse;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.AuthRepository;
import com.webapp.Eventified.service.AuthService;
import com.webapp.Eventified.util.JWTutil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

class AuthServiceTest {

    private AuthRepository authRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JWTutil jwtutil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authRepository = Mockito.mock(AuthRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtutil = Mockito.mock(JWTutil.class);
        authService = new AuthService(authRepository, passwordEncoder, jwtutil, null, null, null, null);
    }

    @Test
    void registerUser_success() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        Mockito.when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(authRepository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(authRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.registerUser(username, email, password);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertTrue(passwordEncoder.matches(password, user.getPasswordHash()));
    }

    @Test
    void registerUser_emailAlreadyUsed_throwsException() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        Mockito.when(authRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

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

        Mockito.when(authRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(authRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

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

        Mockito.when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(jwtutil.generateToken(username)).thenReturn("mocked-jwt-token");

        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = authService.login(request);

        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void login_usernameNotFound_throwsException() {
        String username = "unknown";
        String password = "password123";
        Mockito.when(authRepository.findByUsername(username)).thenReturn(Optional.empty());

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

        Mockito.when(authRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(username, password);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authService.login(request)
        );
        assertEquals("Invalid credentials", exception.getMessage());
    }
}