package com.efinace.mapper;

import com.efinace.dto.response.UserResponse;
import com.efinace.entity.Role;
import com.efinace.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Maps User entity to UserResponse DTO.
 * Uses streams to flatten the role set into role name strings.
 */
@Component
public class UserMapper {

    /**
     * Convert a User entity into a safe response projection.
     */
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .roles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .map(Enum::name)
                                .collect(Collectors.toSet())
                )
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
