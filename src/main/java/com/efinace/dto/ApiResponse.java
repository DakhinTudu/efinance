package com.efinace.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

/**
 * Unified API response wrapper used by all endpoints.
 * Omits null fields (e.g., pageable is null for non-paginated responses).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Object pageable;   // holds PaginationMeta when paginated
    private int status;
    private Instant timestamp;
}
