package com.efinace.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Financial record projection returned by record APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordResponse {

    private Long id;
    private BigDecimal amount;
    private String type;
    private String categoryName;
    private Long categoryId;
    private LocalDate recordDate;
    private String description;
    private String createdByName;
    private Instant createdAt;
    private Instant updatedAt;
}
