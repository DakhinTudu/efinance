package com.efinace.mapper;

import com.efinace.dto.response.CategoryResponse;
import com.efinace.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Maps Category entity to CategoryResponse DTO.
 */
@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
