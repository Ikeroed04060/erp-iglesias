package com.iglesia.controller;

import com.iglesia.dtos.request.LoginRequest;
import com.iglesia.dtos.response.LoginResponse;
import com.iglesia.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());  // Delegamos la lógica al servicio
        return new LoginResponse(token, request.email(), "USER");  // Asumimos que el rol es "USER", puede modificarse según sea necesario
    }
}