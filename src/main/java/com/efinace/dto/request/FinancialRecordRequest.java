package com.efinace.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for creating/updating a financial record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @DecimalMax(value = "99999999.99", message = "Amount must not exceed 99,999,999.99")
    private BigDecimal amount;

    @NotBlank(message = "Type is required (INCOME or EXPENSE)")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Type must be either INCOME or EXPENSE")
    private String type;

    /** Category ID — nullable for uncategorized records */
    private Long categoryId;

    @NotNull(message = "Record date is required")
    @PastOrPresent(message = "Record date cannot be in the future")
    private LocalDate recordDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
