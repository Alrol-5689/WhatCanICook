package com.whatcanicook.controller;

import com.whatcanicook.dto.LoginRequest;
import com.whatcanicook.dto.LoginResponse;
import com.whatcanicook.dto.RegisterRequest;
import com.whatcanicook.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // @AllArgsConstructor no restringe que el campo sea final (evitando errores)
public class AuthController {

    // Recibe peticiones HTTP de Android

    private final AuthService authService;

    // El JSON de registro -> POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) { // (Validando)
        return ResponseEntity.ok(authService.register(request));
    }
    // El JSON de login -> POST /auth/login
    @PostMapping("/login") // responde a POST /auth/login de Android
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) { // JSON -> LoginRequest (Validando)
        return ResponseEntity.ok(authService.login(request)); // Construye respuesta HTTP 200
    } // -> AuthService procesa el login
}
