package com.efinace.controller;

import com.efinace.dto.ApiResponse;
import com.efinace.dto.request.LoginRequest;
import com.efinace.dto.request.RegisterRequest;
import com.efinace.dto.response.AuthResponse;
import com.efinace.service.AuthService;
import com.efinace.util.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller — public endpoints for login and registration.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and registration endpoints")
public class AuthController {

    private final AuthService authService;
    private final ApiResponseBuilder responseBuilder;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Returns JWT token on successful login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return responseBuilder.success(authResponse, "Login successful", HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account and returns JWT token")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return responseBuilder.success(authResponse, "Registration successful", HttpStatus.CREATED);
    }
}
