package com.example.demo.controller;

import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // CREATE BOOK
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookRequest request) {

        BookResponse response = bookService.createBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET BOOK BY ID
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id) {

        BookResponse response = bookService.getBookById(id);

        return ResponseEntity.ok(response);
    }

    // GET ALL BOOKS
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        return ResponseEntity.ok(
                bookService.getAllBooks()
        );
    }

    // UPDATE BOOK
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {

        BookResponse response = bookService.updateBook(id, request);

        return ResponseEntity.ok(response);
    }

    // DELETE BOOK
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}