package com.efinace.service.impl;

import com.efinace.dto.request.LoginRequest;
import com.efinace.dto.request.RegisterRequest;
import com.efinace.dto.response.AuthResponse;
import com.efinace.entity.Role;
import com.efinace.entity.User;
import com.efinace.enums.RoleName;
import com.efinace.enums.UserStatus;
import com.efinace.exception.BadRequestException;
import com.efinace.repository.RoleRepository;
import com.efinace.repository.UserRepository;
import com.efinace.security.CustomUserDetails;
import com.efinace.security.JwtUtil;
import com.efinace.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication service implementation — handles login via AuthenticationManager
 * and registration with BCrypt password hashing.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate via Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        logger.info("User logged in successfully: {}", request.getEmail());

        return buildAuthResponse(token, userDetails);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        // Resolve role — default to VIEWER if not specified
        RoleName roleName = resolveRoleName(request.getRoleName());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));

        // Build and persist the user with hashed password
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
        logger.info("New user registered: {} with role {}", request.getEmail(), roleName);

        // Generate token for immediate login after registration
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        return buildAuthResponse(token, userDetails);
    }

    /** Resolve role name from string, defaulting to VIEWER */
    private RoleName resolveRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return RoleName.VIEWER;
        }
        try {
            return RoleName.valueOf(roleName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + roleName + ". Valid roles: VIEWER, ANALYST, ADMIN");
        }
    }

    /** Build the common auth response from user details */
    private AuthResponse buildAuthResponse(String token, CustomUserDetails userDetails) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .roles(
                        userDetails.getAuthorities().stream()
                                .map(a -> a.getAuthority())
                                .filter(a -> a.startsWith("ROLE_"))
                                .map(a -> a.substring(5))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
