package com.efinace.repository;

import com.efinace.entity.FinancialRecord;
import com.efinace.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for FinancialRecord with filtering and analytics queries.
 */
@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    /** Find a non-deleted record by ID */
    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    /** Paginated listing of non-deleted records with optional filters */
    @Query("""
        SELECT fr FROM FinancialRecord fr
        WHERE fr.deleted = false
          AND (:type IS NULL OR fr.type = :type)
          AND (:categoryId IS NULL OR fr.category.id = :categoryId)
          AND (:startDate IS NULL OR fr.recordDate >= :startDate)
          AND (:endDate IS NULL OR fr.recordDate <= :endDate)
        ORDER BY fr.recordDate DESC
    """)
    Page<FinancialRecord> findAllWithFilters(
            @Param("type") RecordType type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    /** All non-deleted records (for analytics aggregation) */
    List<FinancialRecord> findAllByDeletedFalse();

    /** Filtered non-deleted records for dashboard analytics */
    @Query("""
        SELECT fr FROM FinancialRecord fr
        WHERE fr.deleted = false
          AND (:startDate IS NULL OR fr.recordDate >= :startDate)
          AND (:endDate IS NULL OR fr.recordDate <= :endDate)
        ORDER BY fr.recordDate DESC
    """)
    List<FinancialRecord> findAnalyticsWithFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /** Recent non-deleted records, limited and ordered */
    List<FinancialRecord> findTop10ByDeletedFalseOrderByCreatedAtDesc();
}
