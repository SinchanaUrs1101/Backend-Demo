package com.example.demo.controller;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse savedCategory = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {

        CategoryResponse response = categoryService.getCategoryById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        boolean deleted = categoryService.deleteCategory(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}