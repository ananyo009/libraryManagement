package com.college.library.service;

import com.college.library.dto.BookRequest;
import com.college.library.dto.BookResponse;
import com.college.library.entity.Book;
import com.college.library.entity.IssueStatus;
import com.college.library.exception.BusinessRuleException;
import com.college.library.exception.DuplicateResourceException;
import com.college.library.exception.ResourceNotFoundException;
import com.college.library.repository.BookIssueRepository;
import com.college.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookIssueRepository issueRepository;

    @Transactional
    public BookResponse addBook(BookRequest req) {
        if (bookRepository.existsByIsbn(req.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + req.getIsbn() + " already exists");
        }
        Book book = Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .isbn(req.getIsbn())
                .category(req.getCategory())
                .totalCopies(req.getTotalCopies())
                .availableCopies(req.getTotalCopies())
                .build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return BookResponse.from(findBook(id));
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest req) {
        Book book = findBook(id);

        if (bookRepository.existsByIsbnAndBookIdNot(req.getIsbn(), id)) {
            throw new DuplicateResourceException("Book with ISBN " + req.getIsbn() + " already exists");
        }

        // keep availableCopies consistent when totalCopies changes
        int diff = req.getTotalCopies() - book.getTotalCopies();
        int newAvailable = book.getAvailableCopies() + diff;
        if (newAvailable < 0) {
            throw new BusinessRuleException(
                    "Cannot reduce total copies below the number of copies currently issued");
        }

        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setCategory(req.getCategory());
        book.setTotalCopies(req.getTotalCopies());
        book.setAvailableCopies(newAvailable);
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = findBook(id);
        // Live-evaluation rule: prevent deletion of a book with an active issue
        if (issueRepository.existsByBookBookIdAndStatus(id, IssueStatus.ISSUED)) {
            throw new BusinessRuleException("Cannot delete a book that is currently issued");
        }
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category).stream().map(BookResponse::from).toList();
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }
}
