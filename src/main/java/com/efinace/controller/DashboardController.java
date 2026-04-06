package com.efinace.controller;

import com.efinace.dto.ApiResponse;
import com.efinace.dto.response.DashboardSummaryResponse;
import com.efinace.dto.response.MonthlyTrendResponse;
import com.efinace.service.DashboardService;
import com.efinace.util.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard controller — analytics endpoints for summary and trends.
 * Restricted to users with VIEW_ANALYTICS permission (Analyst + Admin).
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Analytics and summary endpoints")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ApiResponseBuilder responseBuilder;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('VIEW_ANALYTICS')")
    @Operation(summary = "Get dashboard summary", description = "Total income, expenses, net balance, category breakdown, and recent activity")
    public ResponseEntity<ApiResponse<?>> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        DashboardSummaryResponse summary = dashboardService.getSummary(startDate, endDate);
        return responseBuilder.success(summary, "Dashboard summary retrieved successfully", HttpStatus.OK);
    }

    @GetMapping("/trends/monthly")
    @PreAuthorize("hasAuthority('VIEW_ANALYTICS')")
    @Operation(summary = "Get monthly trends", description = "Income, expense, and net balance broken down by month")
    public ResponseEntity<ApiResponse<?>> getMonthlyTrends() {
        List<MonthlyTrendResponse> trends = dashboardService.getMonthlyTrends();
        return responseBuilder.success(trends, "Monthly trends retrieved successfully", HttpStatus.OK);
    }
}
