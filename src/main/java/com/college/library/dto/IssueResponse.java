package com.college.library.dto;

import com.college.library.entity.BookIssue;
import com.college.library.entity.IssueStatus;

import java.time.LocalDate;

public record IssueResponse(Long issueId, Long studentId, String studentName,
                            Long bookId, String bookTitle,
                            LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                            IssueStatus status) {

    public static IssueResponse from(BookIssue i) {
        return new IssueResponse(i.getIssueId(),
                i.getStudent().getStudentId(), i.getStudent().getStudentName(),
                i.getBook().getBookId(), i.getBook().getTitle(),
                i.getIssueDate(), i.getDueDate(), i.getReturnDate(), i.getStatus());
    }
}
