package com.efinace.entity;

import com.efinace.enums.PermissionName;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Permission entity — represents a granular action right.
 * Linked to roles via the role_permissions join table.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private PermissionName name;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
