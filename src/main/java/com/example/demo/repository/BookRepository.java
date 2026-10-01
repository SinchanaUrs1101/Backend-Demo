package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Category;
import com.example.demo.entity.Author;


public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByCategory(Category category);
    boolean existsByAuthorsContaining(Author author);
    boolean existsByIsbn(String isbn);
}