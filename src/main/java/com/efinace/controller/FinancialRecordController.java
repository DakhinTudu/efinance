package com.efinace.controller;

import com.efinace.dto.ApiResponse;
import com.efinace.dto.PaginatedResponse;
import com.efinace.dto.request.FinancialRecordRequest;
import com.efinace.dto.response.FinancialRecordResponse;
import com.efinace.enums.RecordType;
import com.efinace.service.FinancialRecordService;
import com.efinace.util.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Financial records controller — CRUD + filtered listing
 * with role-based permission enforcement.
 */
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "CRUD and filtering of financial records")
public class FinancialRecordController {

    private final FinancialRecordService recordService;
    private final ApiResponseBuilder responseBuilder;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_RECORDS')")
    @Operation(summary = "Create financial record")
    public ResponseEntity<ApiResponse<?>> createRecord(@Valid @RequestBody FinancialRecordRequest request) {
        FinancialRecordResponse record = recordService.createRecord(request);
        return responseBuilder.success(record, "Record created successfully", HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_RECORDS')")
    @Operation(summary = "List records", description = "Paginated listing with optional filters: type, categoryId, startDate, endDate")
    public ResponseEntity<ApiResponse<?>> getRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // Parse optional type filter
        RecordType recordType = null;
        if (type != null && !type.isBlank()) {
            recordType = RecordType.valueOf(type.toUpperCase().trim());
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<FinancialRecordResponse> records = recordService.getRecords(recordType, categoryId, startDate, endDate, pageable);

        PaginatedResponse.PaginationMeta meta = PaginatedResponse.PaginationMeta.builder()
                .pageNumber(records.getNumber())
                .pageSize(records.getSize())
                .totalPages(records.getTotalPages())
                .totalElements(records.getTotalElements())
                .build();

        return responseBuilder.paginated(records.getContent(), meta, "Records retrieved successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_RECORDS')")
    @Operation(summary = "Get record by ID")
    public ResponseEntity<ApiResponse<?>> getRecordById(@PathVariable Long id) {
        FinancialRecordResponse record = recordService.getRecordById(id);
        return responseBuilder.success(record, "Record retrieved successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_RECORDS')")
    @Operation(summary = "Update financial record")
    public ResponseEntity<ApiResponse<?>> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request
    ) {
        FinancialRecordResponse record = recordService.updateRecord(id, request);
        return responseBuilder.success(record, "Record updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_RECORDS')")
    @Operation(summary = "Soft-delete financial record")
    public ResponseEntity<ApiResponse<?>> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return responseBuilder.success(null, "Record deleted successfully", HttpStatus.OK);
    }
}
