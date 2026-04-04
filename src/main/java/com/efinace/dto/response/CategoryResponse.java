package com.efinace.dto.response;

import lombok.*;

import java.time.Instant;

/**
 * Category projection for category management APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
}
