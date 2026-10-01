package com.example.demo.service;

import com.example.demo.dto.LoanResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.Loan;
import com.example.demo.entity.Member;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0;
    private static final int MAX_ACTIVE_LOANS = 3;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public LoanService(
            LoanRepository loanRepository,
            BookRepository bookRepository,
            MemberRepository memberRepository,
            ModelMapper modelMapper) {

        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    // ISSUE BOOK
    @Transactional
    public LoanResponse issueBook(Long bookId, Long memberId) {

        // Find book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + bookId + " not found",
                                "BOOK_NOT_FOUND"
                        ));

        // Find member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member with id " + memberId + " not found",
                                "MEMBER_NOT_FOUND"
                        ));

        // Check maximum active loans
        long activeLoanCount =
                loanRepository.countByMemberAndReturnedAtIsNull(member);

        if (activeLoanCount >= MAX_ACTIVE_LOANS) {
            throw new IllegalStateException(
                    "Member with id " + memberId
                            + " already has the maximum of "
                            + MAX_ACTIVE_LOANS
                            + " active loans"
            );
        }

        // Check for overdue active loans
        List<Loan> activeLoans =
                loanRepository.findByMemberAndReturnedAtIsNull(member);

        boolean hasOverdueLoan = activeLoans.stream()
                .anyMatch(loan ->
                        LocalDateTime.now().isAfter(loan.getDueDate())
                );

        if (hasOverdueLoan) {
            throw new IllegalStateException(
                    "Member with id " + memberId
                            + " has an overdue loan and cannot borrow another book"
            );
        }

        // Check available copies
        if (book.getAvailableCopies() == null
                || book.getAvailableCopies() <= 0) {

            throw new IllegalStateException(
                    "Book with id " + bookId + " is not available"
            );
        }

        // Check duplicate active loan
        if (loanRepository.existsByBookIdAndMemberIdAndReturnedAtIsNull(
                bookId,
                memberId)) {

            throw new IllegalStateException(
                    "Member already has an active loan for this book"
            );
        }

        // Create loan
        Loan loan = new Loan();

        LocalDateTime now = LocalDateTime.now();

        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowedAt(now);
        loan.setDueDate(now.plusDays(LOAN_PERIOD_DAYS));

        // Decrease available copies
        book.setAvailableCopies(
                book.getAvailableCopies() - 1
        );

        bookRepository.save(book);

        // Save loan
        Loan savedLoan = loanRepository.save(loan);

        return mapToResponse(savedLoan);
    }

    // RETURN BOOK
    @Transactional
    public LoanResponse returnBook(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan with id " + loanId + " not found",
                                "LOAN_NOT_FOUND"
                        ));

        // Check if already returned
        if (loan.getReturnedAt() != null) {

            throw new IllegalStateException(
                    "Loan with id " + loanId + " has already been returned"
            );
        }

        LocalDateTime returnedAt = LocalDateTime.now();

        loan.setReturnedAt(returnedAt);

        // Increase available copies
        Book book = loan.getBook();

        book.setAvailableCopies(
                book.getAvailableCopies() + 1
        );

        bookRepository.save(book);

        // Save returned loan
        Loan updatedLoan = loanRepository.save(loan);

        return mapToResponse(updatedLoan);
    }

    // GET LOAN BY ID
    public LoanResponse getLoanById(Long id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan with id " + id + " not found",
                                "LOAN_NOT_FOUND"
                        ));

        return mapToResponse(loan);
    }

    // GET ALL LOANS
    public List<LoanResponse> getAllLoans() {

        return loanRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // CALCULATE FINE
    private double calculateFine(Loan loan) {

        // No fine if book has not been returned
        if (loan.getReturnedAt() == null) {
            return 0.0;
        }

        // No fine if returned on or before due date
        if (!loan.getReturnedAt().isAfter(loan.getDueDate())) {
            return 0.0;
        }

        long overdueDays =
                Duration.between(
                        loan.getDueDate(),
                        loan.getReturnedAt()
                ).toDays();

        return overdueDays * FINE_PER_DAY;
    }

    // DERIVE LOAN STATUS
    private String calculateStatus(Loan loan) {

        // Returned loan
        if (loan.getReturnedAt() != null) {
            return "RETURNED";
        }

        // Overdue active loan
        if (LocalDateTime.now().isAfter(loan.getDueDate())) {
            return "OVERDUE";
        }

        // Normal active loan
        return "ACTIVE";
    }

    // ENTITY → RESPONSE DTO
    private LoanResponse mapToResponse(Loan loan) {

        // Use ModelMapper for fields that directly match
        LoanResponse response =
                modelMapper.map(loan, LoanResponse.class);

        // Handle relationships manually
        response.setBookId(
                loan.getBook().getId()
        );

        response.setMemberId(
                loan.getMember().getId()
        );

        // Handle calculated fields manually
        response.setStatus(
                calculateStatus(loan)
        );

        response.setFineAmount(
                calculateFine(loan)
        );

        return response;
    }
}