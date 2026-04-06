package com.efinace.service.impl;

import com.efinace.dto.response.CategoryTotalResponse;
import com.efinace.dto.response.DashboardSummaryResponse;
import com.efinace.dto.response.FinancialRecordResponse;
import com.efinace.dto.response.MonthlyTrendResponse;
import com.efinace.entity.FinancialRecord;
import com.efinace.enums.RecordType;
import com.efinace.mapper.FinancialRecordMapper;
import com.efinace.repository.FinancialRecordRepository;
import com.efinace.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dashboard analytics service — performs aggregation using Java streams
 * over financial records for summary, category breakdown, and trends.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordMapper recordMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        List<FinancialRecord> records = recordRepository.findAnalyticsWithFilters(startDate, endDate);

        // Aggregate total income using stream reduce
        BigDecimal totalIncome = records.stream()
                .filter(r -> r.getType() == RecordType.INCOME)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aggregate total expenses
        BigDecimal totalExpenses = records.stream()
                .filter(r -> r.getType() == RecordType.EXPENSE)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // Category-wise breakdown using groupingBy + downstream collectors
        List<CategoryTotalResponse> categoryTotals = records.stream()
                .collect(Collectors.groupingBy(
                        r -> new CategoryKey(
                                r.getCategory() != null ? r.getCategory().getId() : 0L,
                                r.getCategory() != null ? r.getCategory().getName() : "Uncategorized",
                                r.getType().name()
                        )
                ))
                .entrySet().stream()
                .map(entry -> CategoryTotalResponse.builder()
                        .categoryId(entry.getKey().id())
                        .categoryName(entry.getKey().name())
                        .type(entry.getKey().type())
                        .total(entry.getValue().stream()
                                .map(FinancialRecord::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .count(entry.getValue().size())
                        .build()
                )
                .sorted(Comparator.comparing(CategoryTotalResponse::getTotal).reversed())
                .collect(Collectors.toList());

        // Recent activity — last 10 records
        List<FinancialRecordResponse> recentActivity = recordRepository
                .findTop10ByDeletedFalseOrderByCreatedAtDesc().stream()
                .map(recordMapper::toResponse)
                .collect(Collectors.toList());

        logger.info("Dashboard summary generated: income={}, expenses={}, net={}", totalIncome, totalExpenses, netBalance);

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .recentActivity(recentActivity)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyTrendResponse> getMonthlyTrends() {
        List<FinancialRecord> records = recordRepository.findAllByDeletedFalse();

        // Group records by year-month, then compute income/expense/net per month
        Map<String, List<FinancialRecord>> byMonth = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRecordDate().getYear() + "-" + String.format("%02d", r.getRecordDate().getMonthValue())
                ));

        List<MonthlyTrendResponse> trends = byMonth.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    List<FinancialRecord> monthRecords = entry.getValue();

                    BigDecimal income = monthRecords.stream()
                            .filter(r -> r.getType() == RecordType.INCOME)
                            .map(FinancialRecord::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal expenses = monthRecords.stream()
                            .filter(r -> r.getType() == RecordType.EXPENSE)
                            .map(FinancialRecord::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return MonthlyTrendResponse.builder()
                            .year(year)
                            .month(month)
                            .totalIncome(income)
                            .totalExpenses(expenses)
                            .netBalance(income.subtract(expenses))
                            .build();
                })
                .sorted(Comparator.comparing(MonthlyTrendResponse::getYear)
                        .thenComparing(MonthlyTrendResponse::getMonth))
                .collect(Collectors.toList());

        logger.info("Monthly trends generated: {} months of data", trends.size());
        return trends;
    }

    /** Internal record for grouping key (Java 16+ record) */
    private record CategoryKey(Long id, String name, String type) {}
}
