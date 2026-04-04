package com.efinace.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Wrapper for paginated data with pagination metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> data;
    private PaginationMeta pageable;

    /**
     * Pagination metadata — page number, size, total pages, total elements.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationMeta implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private int pageNumber;
        private int pageSize;
        private int totalPages;
        private long totalElements;
    }
}
