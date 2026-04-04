package com.efinace.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * Category-wise total projection for dashboard breakdown.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTotalResponse {

    private Long categoryId;
    private String categoryName;
    private String type;
    private BigDecimal total;
    private long count;
}
