package com.whatcanicook.service;

import com.whatcanicook.dto.model.UserDto;
import com.whatcanicook.dto.request.LoginRequest;
import com.whatcanicook.dto.response.AuthResponse;
import com.whatcanicook.dto.request.RegisterRequest;
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

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(
                    false,
                    "El email ya está registrado",
                    null
            );
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(
                    false,
                    "El username está en uso",
                    null
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return new AuthResponse(
                true,
                "Usuario registrado correctamente",
                new UserDto(user.getId(), user.getUsername(), user.getEmail())
        );
    }

    public AuthResponse login(LoginRequest request) {
        return userRepository
                .findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> new AuthResponse(
                        true,
                        "Login correcto",
                        new UserDto(user.getId(), user.getUsername(), user.getEmail())))
                .orElse(new AuthResponse(
                        false,
                        "Credenciales incorrectas",
                        null));
    }
}
