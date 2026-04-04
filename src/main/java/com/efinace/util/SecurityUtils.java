package com.efinace.util;

import com.efinace.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for extracting the authenticated user from the SecurityContext.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class — no instantiation
    }

    /**
     * Get the currently authenticated CustomUserDetails, if present.
     */
    public static Optional<CustomUserDetails> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetails.class::isInstance)
                .map(CustomUserDetails.class::cast);
    }

    /**
     * Get the current user's ID, or throw if not authenticated.
     */
    public static Long getCurrentUserId() {
        return getCurrentUser()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));
    }

    /**
     * Get the current user's email.
     */
    public static String getCurrentUserEmail() {
        return getCurrentUser()
                .map(CustomUserDetails::getEmail)
                .orElse("anonymous");
    }
}
