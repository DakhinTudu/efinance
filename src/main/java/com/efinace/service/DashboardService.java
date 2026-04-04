package com.efinace.service;

import com.efinace.dto.response.DashboardSummaryResponse;
import com.efinace.dto.response.MonthlyTrendResponse;

import java.util.List;

/**
 * Dashboard analytics service — aggregation and trend computation.
 */
public interface DashboardService {

    /** Get overall dashboard summary (totals, category breakdown, recent activity) */
    DashboardSummaryResponse getSummary();

    /** Get monthly income/expense/net trends */
    List<MonthlyTrendResponse> getMonthlyTrends();
}
