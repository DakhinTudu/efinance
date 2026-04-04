package com.efinace.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * Monthly trend data point for time-series analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendResponse {

    private int year;
    private int month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
}
