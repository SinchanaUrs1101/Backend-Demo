package com.example.demo.controller;

import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.MemberResponse;
import com.example.demo.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // CREATE MEMBER
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody MemberRequest request) {

        MemberResponse response = memberService.createMember(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET MEMBER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(
            @PathVariable Long id) {

        MemberResponse response = memberService.getMemberById(id);

        return ResponseEntity.ok(response);
    }

    // GET ALL MEMBERS
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {

        return ResponseEntity.ok(
                memberService.getAllMembers()
        );
    }

    // UPDATE MEMBER
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {

        MemberResponse response =
                memberService.updateMember(id, request);

        return ResponseEntity.ok(response);
    }

    // DELETE MEMBER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long id) {

        memberService.deleteMember(id);

        return ResponseEntity.noContent().build();
    }
}