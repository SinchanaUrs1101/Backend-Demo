package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class BookRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 20)
    private String isbn;

    private Integer publishedYear;

    @NotNull
    @Positive
    private Integer totalCopies;

    @NotNull
    @Positive
    private Integer availableCopies;

    @NotNull
    private Long categoryId;

    private Set<Long> authorIds;

    // Generate getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Set<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(Set<Long> authorIds) {
        this.authorIds = authorIds;
    }
}