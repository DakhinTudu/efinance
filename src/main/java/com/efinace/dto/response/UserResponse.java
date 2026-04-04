package com.efinace.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.Set;

/**
 * User projection returned by user management APIs.
 * Excludes sensitive fields like password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String status;
    private Set<String> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
