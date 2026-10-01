package com.example.demo.repository;

import com.example.demo.entity.Loan;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByMember(Member member);

    List<Loan> findByMemberAndReturnedAtIsNull(Member member);

    boolean existsByMemberAndReturnedAtIsNull(Member member);

    boolean existsByBookIdAndMemberIdAndReturnedAtIsNull(
            Long bookId,
            Long memberId
    );
    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);

    // Count active loans for a member
    long countByMemberAndReturnedAtIsNull(Member member);
    long countByBookIdAndReturnedAtIsNull(Long bookId);
}