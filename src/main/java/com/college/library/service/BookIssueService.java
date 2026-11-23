package com.college.library.service;

import com.college.library.dto.IssueResponse;
import com.college.library.entity.Book;
import com.college.library.entity.BookIssue;
import com.college.library.entity.IssueStatus;
import com.college.library.entity.Student;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.NoCopiesAvailableException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookIssueRepository;
import com.college.library.repository.BookRepository;
import com.college.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookIssueService {

    private static final int MAX_ACTIVE_ISSUES = 3;
    private static final int LOAN_DAYS = 14;

    private final BookIssueRepository issueRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    /** Issue + availableCopies update happen in ONE transaction - both succeed or both roll back. */
    @Transactional
    public IssueResponse issueBook(Long studentId, Long bookId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + studentId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new NoCopiesAvailableException("No copies available for book '" + book.getTitle() + "'");
        }

        long active = issueRepository.countByStudentStudentIdAndStatus(studentId, IssueStatus.ISSUED);
        if (active >= MAX_ACTIVE_ISSUES) {
            throw new BusinessRuleException(
                    "Student already has the maximum of " + MAX_ACTIVE_ISSUES + " active book issues");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        BookIssue issue = BookIssue.builder()
                .student(student)
                .book(book)
                .issueDate(today)
                .dueDate(today.plusDays(LOAN_DAYS))
                .status(IssueStatus.ISSUED)
                .build();
        return IssueResponse.from(issueRepository.save(issue));
    }

    @Transactional
    public IssueResponse returnBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with id " + issueId));

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new BusinessRuleException("This book has already been returned");
        }

        issue.setReturnDate(LocalDate.now());
        issue.setStatus(IssueStatus.RETURNED);

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return IssueResponse.from(issueRepository.save(issue));
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream().map(IssueResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        }
        return issueRepository.findByStudentStudentId(studentId).stream().map(IssueResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getOverdueIssues() {
        return issueRepository.findByStatusAndDueDateBefore(IssueStatus.ISSUED, LocalDate.now())
                .stream().map(IssueResponse::from).toList();
    }
}
