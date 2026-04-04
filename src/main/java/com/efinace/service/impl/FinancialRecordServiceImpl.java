package com.efinace.service.impl;

import com.efinace.dto.request.FinancialRecordRequest;
import com.efinace.dto.response.FinancialRecordResponse;
import com.efinace.entity.Category;
import com.efinace.entity.FinancialRecord;
import com.efinace.entity.User;
import com.efinace.enums.RecordType;
import com.efinace.exception.BadRequestException;
import com.efinace.exception.ResourceNotFoundException;
import com.efinace.mapper.FinancialRecordMapper;
import com.efinace.repository.CategoryRepository;
import com.efinace.repository.FinancialRecordRepository;
import com.efinace.repository.UserRepository;
import com.efinace.service.FinancialRecordService;
import com.efinace.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Financial record service — handles CRUD with soft-delete,
 * filtering, and ownership tracking via the authenticated user.
 */
@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialRecordServiceImpl.class);

    private final FinancialRecordRepository recordRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FinancialRecordMapper recordMapper;

    @Override
    @Transactional
    public FinancialRecordResponse createRecord(FinancialRecordRequest request) {
        RecordType type = parseRecordType(request.getType());
        Category category = resolveCategory(request.getCategoryId());
        User currentUser = getCurrentUser();

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(type)
                .category(category)
                .recordDate(request.getRecordDate())
                .description(request.getDescription())
                .createdBy(currentUser)
                .deleted(false)
                .build();

        record = recordRepository.save(record);
        logger.info("Financial record created: id={} by user={}", record.getId(), currentUser.getEmail());

        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecordById(Long id) {
        FinancialRecord record = findRecordOrThrow(id);
        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getRecords(
            RecordType type, Long categoryId, LocalDate startDate, LocalDate endDate, Pageable pageable
    ) {
        return recordRepository.findAllWithFilters(type, categoryId, startDate, endDate, pageable)
                .map(recordMapper::toResponse);
    }

    @Override
    @Transactional
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request) {
        FinancialRecord record = findRecordOrThrow(id);

        record.setAmount(request.getAmount());
        record.setType(parseRecordType(request.getType()));
        record.setCategory(resolveCategory(request.getCategoryId()));
        record.setRecordDate(request.getRecordDate());
        record.setDescription(request.getDescription());

        record = recordRepository.save(record);
        logger.info("Financial record updated: id={}", id);

        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = findRecordOrThrow(id);
        // Soft-delete: mark as deleted instead of removing from DB
        record.setDeleted(true);
        recordRepository.save(record);
        logger.info("Financial record soft-deleted: id={}", id);
    }

    // ---- Helpers ----

    private FinancialRecord findRecordOrThrow(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record", "id", id));
    }

    private RecordType parseRecordType(String type) {
        try {
            return RecordType.valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid record type: " + type + ". Must be INCOME or EXPENSE.");
        }
    }

    /** Resolve category by ID; returns null if not provided */
    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    /** Get the currently authenticated user entity */
    private User getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
