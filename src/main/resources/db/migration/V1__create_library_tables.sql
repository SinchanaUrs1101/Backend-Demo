CREATE TABLE members (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         email VARCHAR(150) NOT NULL UNIQUE,
                         phone VARCHAR(20),
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       author VARCHAR(150) NOT NULL,
                       category VARCHAR(100),
                       available BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loans (
                       id BIGSERIAL PRIMARY KEY,
                       book_id BIGINT NOT NULL,
                       member_id BIGINT NOT NULL,
                       borrowed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       due_date TIMESTAMP NOT NULL,
                       returned_at TIMESTAMP,

                       CONSTRAINT fk_loan_book
                           FOREIGN KEY (book_id)
                               REFERENCES books(id),

                       CONSTRAINT fk_loan_member
                           FOREIGN KEY (member_id)
                               REFERENCES members(id)
);