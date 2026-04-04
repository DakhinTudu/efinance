package com.efinace.service;

import com.efinace.dto.request.UserUpdateRequest;
import com.efinace.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * User management service — CRUD operations on users (admin only).
 */
public interface UserService {

    /** Get all users with pagination */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /** Get a single user by ID */
    UserResponse getUserById(Long id);

    /** Update user details, status, or roles */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /** Deactivate (soft-disable) a user */
    void deactivateUser(Long id);
}
