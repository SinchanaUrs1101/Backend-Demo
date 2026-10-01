package com.example.demo.controller;

import com.example.demo.dto.AuthorRequest;
import com.example.demo.dto.AuthorResponse;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(
            @Valid @RequestBody AuthorRequest request) {

        AuthorResponse response = authorService.createAuthor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(
            @PathVariable Long id) {

        AuthorResponse response = authorService.getAuthorById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Page<AuthorResponse> getAllAuthors(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        return authorService.getAllAuthors(pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {

        AuthorResponse response = authorService.updateAuthor(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {

        authorService.deleteAuthor(id);

        return ResponseEntity.noContent().build();
    }
}