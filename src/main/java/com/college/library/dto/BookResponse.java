package com.college.library.dto;

import com.college.library.entity.Book;

public record BookResponse(Long bookId, String title, String author, String isbn,
                           String category, Integer totalCopies, Integer availableCopies) {

    public static BookResponse from(Book b) {
        return new BookResponse(b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                b.getCategory(), b.getTotalCopies(), b.getAvailableCopies());
    }
}
