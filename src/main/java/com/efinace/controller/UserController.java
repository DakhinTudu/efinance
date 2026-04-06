package com.efinace.controller;

import com.efinace.dto.ApiResponse;
import com.efinace.dto.PaginatedResponse;
import com.efinace.dto.request.UserCreateRequest;
import com.efinace.dto.request.UserUpdateRequest;
import com.efinace.dto.response.UserResponse;
import com.efinace.service.UserService;
import com.efinace.util.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * User management controller — admin-only CRUD on user accounts.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Admin-only user management endpoints")
public class UserController {

    private final UserService userService;
    private final ApiResponseBuilder responseBuilder;

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Operation(summary = "List all users", description = "Paginated listing of all users (admin only)")
    public ResponseEntity<ApiResponse<?>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.getAllUsers(pageable);

        PaginatedResponse.PaginationMeta meta = PaginatedResponse.PaginationMeta.builder()
                .pageNumber(users.getNumber())
                .pageSize(users.getSize())
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .build();

        return responseBuilder.paginated(users.getContent(), meta, "Users retrieved successfully", HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Operation(summary = "Create user", description = "Admin creates a new user and assigns roles")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return responseBuilder.success(user, "User created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return responseBuilder.success(user, "User retrieved successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Operation(summary = "Update user", description = "Update user details, status, or roles")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse user = userService.updateUser(id, request);
        return responseBuilder.success(user, "User updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Operation(summary = "Deactivate user", description = "Sets user status to INACTIVE")
    public ResponseEntity<ApiResponse<?>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return responseBuilder.success(null, "User deactivated successfully", HttpStatus.OK);
    }
}
