package com.efinace.enums;

/**
 * Enumeration of granular permissions used for @PreAuthorize checks.
 * Maps directly to the 'permissions' table name column.
 */
public enum PermissionName {
    READ_RECORDS,
    WRITE_RECORDS,
    DELETE_RECORDS,
    VIEW_ANALYTICS,
    MANAGE_USERS,
    MANAGE_CATEGORIES
}
