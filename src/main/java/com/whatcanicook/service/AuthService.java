package com.whatcanicook.service;

import com.whatcanicook.dto.LoginRequest;
import com.whatcanicook.dto.LoginResponse;
import com.whatcanicook.dto.RegisterRequest;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new LoginResponse(false, "El email ya está registrado");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return new LoginResponse(false, "El username está en uso");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return new LoginResponse(true, "Usuario registrado correctamente");
    }

    public LoginResponse login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> new LoginResponse(true, "Login correcto"))
                .orElse(new LoginResponse(false, "Credenciales incorrectas"));
    }
}
