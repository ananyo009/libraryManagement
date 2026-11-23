package com.college.library.repository;

import com.college.library.entity.BookIssue;
import com.college.library.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    List<BookIssue> findByStudentStudentId(Long studentId);

    long countByStudentStudentIdAndStatus(Long studentId, IssueStatus status);

    boolean existsByStudentStudentIdAndStatus(Long studentId, IssueStatus status);

    boolean existsByBookBookIdAndStatus(Long bookId, IssueStatus status);

    // overdue = still ISSUED and due date is before today
    List<BookIssue> findByStatusAndDueDateBefore(IssueStatus status, LocalDate date);
}
