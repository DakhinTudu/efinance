package com.efinace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Request payload for updating a user's profile, status, or roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 2, max = 100, message = "Full name must be 2–100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    /** ACTIVE or INACTIVE */
    private String status;

    /** Set of role names to assign */
    private Set<String> roleNames;
}
