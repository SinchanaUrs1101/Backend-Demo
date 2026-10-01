package com.example.demo.service;

import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Category;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.LoanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;
    private final LoanRepository loanRepository;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            AuthorRepository authorRepository,
            ModelMapper modelMapper,
            LoanRepository loanRepository) {

        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
        this.loanRepository = loanRepository;
    }

    // CREATE BOOK
    public BookResponse createBook(BookRequest request) {

        // Check duplicate ISBN
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException(
                    "Book with ISBN " + request.getIsbn() + " already exists"
            );
        }

        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());

        // Find Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id "
                                        + request.getCategoryId()
                                        + " not found",
                                "CATEGORY_NOT_FOUND"
                        ));

        book.setCategory(category);

        // Find Authors
        Set<Author> authors = new HashSet<>();

        for (Long authorId : request.getAuthorIds()) {

            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Author with id "
                                            + authorId
                                            + " not found",
                                    "AUTHOR_NOT_FOUND"
                            ));

            authors.add(author);
        }

        book.setAuthors(authors);

        // Set creation time
        book.setCreatedAt(LocalDateTime.now());

        // Save Book
        Book savedBook = bookRepository.save(book);

        // Entity → DTO
        BookResponse response = modelMapper.map(
                savedBook,
                BookResponse.class
        );

        // Handle relationships manually
        response.setCategoryId(
                savedBook.getCategory().getId()
        );

        response.setAuthorIds(
                savedBook.getAuthors()
                        .stream()
                        .map(Author::getId)
                        .collect(Collectors.toSet())
        );

        return response;
    }

    // GET BOOK BY ID
    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " not found",
                                "BOOK_NOT_FOUND"
                        ));

        // Entity → DTO
        BookResponse response = modelMapper.map(
                book,
                BookResponse.class
        );

        // Handle relationships manually
        response.setCategoryId(
                book.getCategory().getId()
        );

        response.setAuthorIds(
                book.getAuthors()
                        .stream()
                        .map(Author::getId)
                        .collect(Collectors.toSet())
        );

        return response;
    }

    // GET ALL BOOKS
    public List<BookResponse> getAllBooks() {

        return bookRepository.findAll()
                .stream()
                .map(book -> {

                    BookResponse response = modelMapper.map(
                            book,
                            BookResponse.class
                    );

                    // Handle relationships manually
                    response.setCategoryId(
                            book.getCategory().getId()
                    );

                    response.setAuthorIds(
                            book.getAuthors()
                                    .stream()
                                    .map(Author::getId)
                                    .collect(Collectors.toSet())
                    );

                    return response;
                })
                .toList();
    }

    // UPDATE BOOK
    public BookResponse updateBook(
            Long id,
            BookRequest request) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " not found",
                                "BOOK_NOT_FOUND"
                        ));

        // Check duplicate ISBN
        if (!existingBook.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {

            throw new DuplicateResourceException(
                    "Book with ISBN " + request.getIsbn() + " already exists"
            );
        }

        // BR-14: Validate copy count
        long activeLoans =
                loanRepository.countByBookIdAndReturnedAtIsNull(id);

        if (request.getTotalCopies() < activeLoans) {

            throw new IllegalStateException(
                    "Total copies cannot be less than the number of copies currently on loan"
            );
        }

        int currentlyBorrowed = (int) activeLoans;

        int newAvailableCopies =
                request.getTotalCopies() - currentlyBorrowed;

        existingBook.setTitle(request.getTitle());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setPublishedYear(request.getPublishedYear());
        existingBook.setTotalCopies(request.getTotalCopies());
        existingBook.setAvailableCopies(newAvailableCopies);

        // Update Category
        Category category = categoryRepository.findById(
                        request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id "
                                        + request.getCategoryId()
                                        + " not found",
                                "CATEGORY_NOT_FOUND"
                        ));

        existingBook.setCategory(category);

        // Update Authors
        Set<Author> authors = new HashSet<>();

        for (Long authorId : request.getAuthorIds()) {

            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Author with id "
                                            + authorId
                                            + " not found",
                                    "AUTHOR_NOT_FOUND"
                            ));

            authors.add(author);
        }

        existingBook.setAuthors(authors);

        // Save updated book
        Book updatedBook = bookRepository.save(existingBook);

        // Entity → DTO
        BookResponse response = modelMapper.map(
                updatedBook,
                BookResponse.class
        );

        // Handle relationships manually
        response.setCategoryId(
                updatedBook.getCategory().getId()
        );

        response.setAuthorIds(
                updatedBook.getAuthors()
                        .stream()
                        .map(Author::getId)
                        .collect(Collectors.toSet())
        );

        return response;
    }

    // DELETE BOOK
    public boolean deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " not found",
                                "BOOK_NOT_FOUND"
                        ));

        // BR-9: Cannot delete a book with an active loan
        if (loanRepository.existsByBookIdAndReturnedAtIsNull(id)) {

            throw new IllegalStateException(
                    "Book with id " + id
                            + " cannot be deleted because it has an active loan"
            );
        }

        bookRepository.delete(book);

        return true;
    }
}