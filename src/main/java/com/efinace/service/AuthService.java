package com.efinace.service;

import com.efinace.dto.request.LoginRequest;
import com.efinace.dto.request.RegisterRequest;
import com.efinace.dto.response.AuthResponse;

/**
 * Authentication service — handles login and user registration.
 */
public interface AuthService {

    /** Authenticate user and return JWT token */
    AuthResponse login(LoginRequest request);

    /** Register a new user and return JWT token */
    AuthResponse register(RegisterRequest request);
}
