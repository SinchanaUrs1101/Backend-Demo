package com.example.demo.service;

import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.MemberResponse;
import com.example.demo.entity.Member;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final ModelMapper modelMapper;

    public MemberService(
            MemberRepository memberRepository,
            LoanRepository loanRepository,
            ModelMapper modelMapper) {

        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.modelMapper = modelMapper;
    }

    // CREATE MEMBER
    public MemberResponse createMember(MemberRequest request) {

        // Business Rule: Email must be unique
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Member with email " + request.getEmail() + " already exists"
            );
        }

        // DTO → Entity
        Member member = modelMapper.map(
                request,
                Member.class
        );

        // Application-controlled field
        member.setCreatedAt(LocalDateTime.now());

        Member savedMember =
                memberRepository.save(member);

        // Entity → Response DTO
        return modelMapper.map(
                savedMember,
                MemberResponse.class
        );
    }

    // GET MEMBER BY ID
    public MemberResponse getMemberById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member with id " + id + " not found",
                                "MEMBER_NOT_FOUND"
                        ));

        // Entity → Response DTO
        return modelMapper.map(
                member,
                MemberResponse.class
        );
    }

    // GET ALL MEMBERS
    public List<MemberResponse> getAllMembers() {

        return memberRepository.findAll()
                .stream()
                .map(member ->
                        modelMapper.map(
                                member,
                                MemberResponse.class
                        )
                )
                .toList();
    }

    // UPDATE MEMBER
    public MemberResponse updateMember(
            Long id,
            MemberRequest request) {

        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member with id " + id + " not found",
                                "MEMBER_NOT_FOUND"
                        ));

        // Check duplicate email
        if (!existingMember.getEmail().equals(request.getEmail())
                && memberRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Member with email " + request.getEmail()
                            + " already exists"
            );
        }

        // DTO → Existing Entity
        modelMapper.map(
                request,
                existingMember
        );

        // Do NOT change createdAt during update

        Member updatedMember =
                memberRepository.save(existingMember);

        // Entity → Response DTO
        return modelMapper.map(
                updatedMember,
                MemberResponse.class
        );
    }

    // DELETE MEMBER
    public boolean deleteMember(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Member with id " + id + " not found",
                                "MEMBER_NOT_FOUND"
                        ));

        // Check for active loans
        if (loanRepository.existsByMemberAndReturnedAtIsNull(member)) {

            throw new IllegalStateException(
                    "Member with id " + id
                            + " cannot be deleted because they have active loans"
            );
        }

        memberRepository.delete(member);

        return true;
    }
}