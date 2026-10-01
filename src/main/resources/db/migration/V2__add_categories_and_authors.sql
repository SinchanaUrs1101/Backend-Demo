CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         nationality VARCHAR(60)
);

CREATE TABLE book_authors (
                              book_id BIGINT NOT NULL,
                              author_id BIGINT NOT NULL,

                              PRIMARY KEY (book_id, author_id),

                              CONSTRAINT fk_book_authors_book
                                  FOREIGN KEY (book_id)
                                      REFERENCES books(id),

                              CONSTRAINT fk_book_authors_author
                                  FOREIGN KEY (author_id)
                                      REFERENCES authors(id)
);

ALTER TABLE books
    ADD COLUMN category_id BIGINT,
    ADD COLUMN published_year INTEGER,
    ADD COLUMN total_copies INTEGER,
    ADD COLUMN available_copies INTEGER;

ALTER TABLE books
    ADD CONSTRAINT fk_books_category
        FOREIGN KEY (category_id)
            REFERENCES categories(id);