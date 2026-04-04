package com.efinace.security;

import com.efinace.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Custom UserDetails implementation wrapping the User entity.
 * Authorities are derived from both roles (ROLE_ prefix) and
 * their associated permissions for fine-grained @PreAuthorize checks.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = user.getStatus().name().equals("ACTIVE");

        // Combine role-based authorities (ROLE_ADMIN) with permission-based (READ_RECORDS)
        this.authorities = user.getRoles().stream()
                .flatMap(role -> Stream.concat(
                        // Role authority: ROLE_VIEWER, ROLE_ANALYST, ROLE_ADMIN
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName().name())),
                        // Permission authorities: READ_RECORDS, WRITE_RECORDS, etc.
                        role.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getName().name()))
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}
