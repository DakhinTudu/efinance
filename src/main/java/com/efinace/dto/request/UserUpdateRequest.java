package com.efinace.dto.request;

import jakarta.validation.constraints.*;
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
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z .'-]{1,99}$",
            message = "Full name must start with a letter and contain only letters, spaces, dots, hyphens, or apostrophes")
    private String fullName;

    @Email(message = "Invalid email format", regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    /** ACTIVE or INACTIVE */
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be ACTIVE or INACTIVE")
    private String status;

    /** Set of role names to assign */
    private Set<String> roleNames;
}
