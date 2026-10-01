package com.example.demo.service;

import com.example.demo.dto.AuthorRequest;
import com.example.demo.dto.AuthorResponse;
import com.example.demo.entity.Author;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;

    public AuthorService(
            AuthorRepository authorRepository,
            ModelMapper modelMapper,
            BookRepository bookRepository) {

        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
    }

    // CREATE AUTHOR
    public AuthorResponse createAuthor(AuthorRequest request) {

        // DTO → Entity
        Author author = modelMapper.map(
                request,
                Author.class
        );

        Author savedAuthor = authorRepository.save(author);

        // Entity → Response DTO
        return modelMapper.map(
                savedAuthor,
                AuthorResponse.class
        );
    }

    // GET AUTHOR BY ID
    public AuthorResponse getAuthorById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Author with id " + id + " not found",
                                "AUTHOR_NOT_FOUND"
                        ));

        // Entity → Response DTO
        return modelMapper.map(
                author,
                AuthorResponse.class
        );
    }

    // GET ALL AUTHORS - PAGINATED
    public Page<AuthorResponse> getAllAuthors(Pageable pageable) {

        return authorRepository.findAll(pageable)
                .map(author ->
                        modelMapper.map(
                                author,
                                AuthorResponse.class
                        )
                );
    }

    // UPDATE AUTHOR
    public AuthorResponse updateAuthor(
            Long id,
            AuthorRequest request) {

        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Author with id " + id + " not found",
                                "AUTHOR_NOT_FOUND"
                        ));

        // DTO → Existing Entity
        modelMapper.map(
                request,
                existingAuthor
        );

        Author updatedAuthor =
                authorRepository.save(existingAuthor);

        // Entity → Response DTO
        return modelMapper.map(
                updatedAuthor,
                AuthorResponse.class
        );
    }

    // DELETE AUTHOR
    public boolean deleteAuthor(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Author with id " + id + " not found",
                                "AUTHOR_NOT_FOUND"
                        ));

        // BR-12: Cannot delete author if books use this author
        if (bookRepository.existsByAuthorsContaining(author)) {

            throw new IllegalStateException(
                    "Author with id " + id
                            + " cannot be deleted because books are associated with this author"
            );
        }

        authorRepository.delete(author);

        return true;
    }
}