package com.efinace.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard summary — aggregated totals, category breakdown, and recent activity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private List<CategoryTotalResponse> categoryTotals;
    private List<FinancialRecordResponse> recentActivity;
}
