package com.efinace.service.impl;

import com.efinace.dto.request.UserCreateRequest;
import com.efinace.dto.request.UserUpdateRequest;
import com.efinace.dto.response.UserResponse;
import com.efinace.entity.Role;
import com.efinace.entity.User;
import com.efinace.enums.RoleName;
import com.efinace.enums.UserStatus;
import com.efinace.exception.BadRequestException;
import com.efinace.exception.ResourceNotFoundException;
import com.efinace.mapper.UserMapper;
import com.efinace.repository.RoleRepository;
import com.efinace.repository.UserRepository;
import com.efinace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * User management service — admin-only CRUD operations on users.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }

        Set<Role> roles;
        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            roles = request.getRoleNames().stream()
                    .map(name -> {
                        RoleName roleName = parseRoleName(name);
                        return roleRepository.findByName(roleName)
                                .orElseThrow(() -> new BadRequestException("Role not found: " + name));
                    })
                    .collect(Collectors.toSet());
        } else {
            Role defaultRole = roleRepository.findByName(RoleName.VIEWER)
                    .orElseThrow(() -> new BadRequestException("Default role not found"));
            roles = Set.of(defaultRole);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .roles(roles)
                .build();

        user = userRepository.save(user);
        logger.info("Admin created new user: {}", user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUserOrThrow(id);

        // Update full name if provided
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        // Update email if provided (check for duplicates)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // Update status if provided
        if (request.getStatus() != null) {
            try {
                user.setStatus(UserStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + request.getStatus());
            }
        }

        // Update roles if provided
        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            Set<Role> roles = request.getRoleNames().stream()
                    .map(name -> {
                        RoleName roleName = parseRoleName(name);
                        return roleRepository.findByName(roleName)
                                .orElseThrow(() -> new BadRequestException("Role not found: " + name));
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        userRepository.save(user);
        logger.info("User updated: id={}", id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = findUserOrThrow(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        logger.info("User deactivated: id={}", id);
    }

    // ---- Helpers ----

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private RoleName parseRoleName(String name) {
        try {
            return RoleName.valueOf(name.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name: " + name);
        }
    }
}
