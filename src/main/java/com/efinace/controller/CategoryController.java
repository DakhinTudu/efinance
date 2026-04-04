package com.efinace.controller;

import com.efinace.dto.ApiResponse;
import com.efinace.dto.request.CategoryRequest;
import com.efinace.dto.response.CategoryResponse;
import com.efinace.service.CategoryService;
import com.efinace.util.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category controller — CRUD for financial record categories.
 * Read is open to all authenticated users with READ_RECORDS;
 * write/delete requires MANAGE_CATEGORIES (Admin only).
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Financial record category management")
public class CategoryController {

    private final CategoryService categoryService;
    private final ApiResponseBuilder responseBuilder;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_RECORDS')")
    @Operation(summary = "List all categories")
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return responseBuilder.success(categories, "Categories retrieved successfully", HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_CATEGORIES')")
    @Operation(summary = "Create category")
    public ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return responseBuilder.success(category, "Category created successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CATEGORIES')")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse category = categoryService.updateCategory(id, request);
        return responseBuilder.success(category, "Category updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_CATEGORIES')")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return responseBuilder.success(null, "Category deleted successfully", HttpStatus.OK);
    }
}
