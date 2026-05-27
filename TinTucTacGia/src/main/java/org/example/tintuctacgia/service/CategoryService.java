package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.dto.category.CategoryRequest;
import org.example.tintuctacgia.dto.category.CategoryResponse;
import org.example.tintuctacgia.entity.Category;
import org.example.tintuctacgia.mapper.CategoryMapper;
import org.example.tintuctacgia.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Danh mục '" + request.getName() + "' đã tồn tại");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id) {
        return CategoryMapper.toResponse(
                categoryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục id: " + id))
        );
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục id: " + id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy danh mục id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
