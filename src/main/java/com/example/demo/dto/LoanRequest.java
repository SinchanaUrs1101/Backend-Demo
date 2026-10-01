package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public class LoanRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Member ID is required")
    private Long memberId;

    // Getters and setters

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}