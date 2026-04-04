package com.efinace.dto.response;

import lombok.*;

/**
 * Authentication response containing the JWT token and user info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private String email;
    private String fullName;
    private java.util.Set<String> roles;
}
