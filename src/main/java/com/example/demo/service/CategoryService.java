package com.example.demo.service;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.example.demo.repository.BookRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ModelMapper modelMapper,
            BookRepository bookRepository) {

        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
    }

    // CREATE CATEGORY
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Category with name " + request.getName() + " already exists"
            );
        }

        Category category = modelMapper.map(request, Category.class);

        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(
                savedCategory,
                CategoryResponse.class
        );
    }
    // GET CATEGORY BY ID
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + id + " not found",
                                "CATEGORY_NOT_FOUND"
                        ));

        return modelMapper.map(
                category,
                CategoryResponse.class
        );
    }

    // GET ALL CATEGORIES
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(category ->
                        modelMapper.map(
                                category,
                                CategoryResponse.class
                        )
                )
                .toList();
    }

    // UPDATE CATEGORY
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + id + " not found",
                                "CATEGORY_NOT_FOUND"
                        ));

        if (!existingCategory.getName().equals(request.getName())
                && categoryRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException(
                    "Category with name " + request.getName() + " already exists"
            );
        }

        modelMapper.map(request, existingCategory);

        Category updatedCategory =
                categoryRepository.save(existingCategory);

        return modelMapper.map(
                updatedCategory,
                CategoryResponse.class
        );
    }

    // DELETE CATEGORY
    // DELETE CATEGORY
    public boolean deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + id + " not found",
                                "CATEGORY_NOT_FOUND"
                        ));

        // BR-11: Cannot delete category if books use it
        if (bookRepository.existsByCategory(category)) {

            throw new IllegalStateException(
                    "Category with id " + id
                            + " cannot be deleted because books are associated with it"
            );
        }

        categoryRepository.delete(category);

        return true;
    }
}