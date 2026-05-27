package org.example.tintuctacgia.mapper;

import org.example.tintuctacgia.dto.category.CategoryResponse;
import org.example.tintuctacgia.entity.Category;

public class CategoryMapper {
    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
