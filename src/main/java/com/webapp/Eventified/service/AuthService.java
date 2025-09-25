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
