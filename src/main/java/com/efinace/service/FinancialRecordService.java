package com.efinace.service;

import com.efinace.dto.request.FinancialRecordRequest;
import com.efinace.dto.response.FinancialRecordResponse;
import com.efinace.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Financial record service — CRUD + filtered listing.
 */
public interface FinancialRecordService {

    /** Create a new financial record */
    FinancialRecordResponse createRecord(FinancialRecordRequest request);

    /** Get a single record by ID */
    FinancialRecordResponse getRecordById(Long id);

    /** List records with optional filters and pagination */
    Page<FinancialRecordResponse> getRecords(
            RecordType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    /** Update an existing record */
    FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request);

    /** Soft-delete a record */
    void deleteRecord(Long id);
}
