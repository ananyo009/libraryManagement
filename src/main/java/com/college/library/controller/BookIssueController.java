package com.college.library.controller;

import com.college.library.dto.IssueResponse;
import com.college.library.service.BookIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class BookIssueController {

    private final BookIssueService issueService;

    @PostMapping("/{studentId}/{bookId}")
    public ResponseEntity<IssueResponse> issueBook(@PathVariable Long studentId,
                                                   @PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.issueBook(studentId, bookId));
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<IssueResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(issueService.getIssuesByStudent(studentId));
    }

    @PutMapping("/return/{issueId}")
    public ResponseEntity<IssueResponse> returnBook(@PathVariable Long issueId) {
        return ResponseEntity.ok(issueService.returnBook(issueId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<IssueResponse>> getOverdue() {
        return ResponseEntity.ok(issueService.getOverdueIssues());
    }
}
