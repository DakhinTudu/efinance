package com.efinace.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Registration request payload.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be 2–100 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z .'-]{1,99}$",
            message = "Full name must start with a letter and contain only letters, spaces, dots, hyphens, or apostrophes")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format", regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be 6–100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=])[A-Za-z\\d@$!%*?&#^()_+\\-=]{6,}$",
            message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character")
    private String password;

    /** Optional role name — defaults to VIEWER if not provided */
    private String roleName;
}
