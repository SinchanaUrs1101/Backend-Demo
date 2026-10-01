package com.example.demo.controller;

import com.example.demo.dto.LoanRequest;
import com.example.demo.dto.LoanResponse;
import com.example.demo.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ISSUE BOOK
    @PostMapping
    public ResponseEntity<LoanResponse> issueBook(
            @Valid @RequestBody LoanRequest request) {

        LoanResponse response = loanService.issueBook(
                request.getBookId(),
                request.getMemberId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET LOAN BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoanById(
            @PathVariable Long id) {

        LoanResponse response = loanService.getLoanById(id);

        return ResponseEntity.ok(response);
    }

    // GET ALL LOANS
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {

        return ResponseEntity.ok(
                loanService.getAllLoans()
        );
    }

    // RETURN BOOK
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(
            @PathVariable Long id) {

        LoanResponse response = loanService.returnBook(id);

        return ResponseEntity.ok(response);
    }
}