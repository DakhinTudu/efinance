package com.efinace.mapper;

import com.efinace.dto.response.FinancialRecordResponse;
import com.efinace.entity.FinancialRecord;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Maps FinancialRecord entity to FinancialRecordResponse DTO.
 * Safely handles nullable category and createdBy relationships.
 */
@Component
public class FinancialRecordMapper {

    /**
     * Convert a FinancialRecord entity into a response projection.
     */
    public FinancialRecordResponse toResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType().name())
                .categoryId(
                        Optional.ofNullable(record.getCategory())
                                .map(c -> c.getId())
                                .orElse(null)
                )
                .categoryName(
                        Optional.ofNullable(record.getCategory())
                                .map(c -> c.getName())
                                .orElse("Uncategorized")
                )
                .recordDate(record.getRecordDate())
                .description(record.getDescription())
                .createdByName(
                        Optional.ofNullable(record.getCreatedBy())
                                .map(u -> u.getFullName())
                                .orElse("System")
                )
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
