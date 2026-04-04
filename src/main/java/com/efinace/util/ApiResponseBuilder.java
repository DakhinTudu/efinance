package com.efinace.util;

import com.efinace.dto.ApiResponse;
import com.efinace.dto.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Centralized API response builder — provides consistent success,
 * error, and paginated response construction.
 */
@Component
public class ApiResponseBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ApiResponseBuilder.class);

    /** Build a success response */
    public <T> ResponseEntity<ApiResponse<?>> success(T data, String message, HttpStatus status) {
        String traceId = UUID.randomUUID().toString();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(status.value())
                .timestamp(Instant.now())
                .build();

        logger.info("TraceId {}: {} - HTTP {}", traceId, message, status.value());
        return ResponseEntity.status(status).body(response);
    }

    /** Build an error response */
    public <T> ResponseEntity<ApiResponse<?>> error(String message, HttpStatus status, T data) {
        String traceId = UUID.randomUUID().toString();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .status(status.value())
                .timestamp(Instant.now())
                .build();

        logger.error("TraceId {}: {} - HTTP {}", traceId, message, status.value());
        return ResponseEntity.status(status).body(response);
    }

    /** Build a paginated success response */
    public <T> ResponseEntity<ApiResponse<?>> paginated(
            T data,
            PaginatedResponse.PaginationMeta pageable,
            String message,
            HttpStatus status
    ) {
        String traceId = UUID.randomUUID().toString();

        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .pageable(pageable)
                .status(status.value())
                .timestamp(Instant.now())
                .build();

        logger.info("TraceId {}: {} (Paginated) - HTTP {}", traceId, message, status.value());
        return ResponseEntity.status(status).body(response);
    }
}
