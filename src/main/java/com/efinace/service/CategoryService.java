package com.efinace.service;

import com.efinace.dto.request.CategoryRequest;
import com.efinace.dto.response.CategoryResponse;

import java.util.List;

/**
 * Category management service — CRUD for financial record categories.
 */
public interface CategoryService {

    /** List all categories */
    List<CategoryResponse> getAllCategories();

    /** Create a new category */
    CategoryResponse createCategory(CategoryRequest request);

    /** Update an existing category */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /** Delete a category */
    void deleteCategory(Long id);
}
