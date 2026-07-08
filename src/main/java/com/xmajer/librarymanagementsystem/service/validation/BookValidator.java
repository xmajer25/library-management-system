package com.xmajer.librarymanagementsystem.service.validation;

import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.exception.DuplicateResourceException;
import com.xmajer.librarymanagementsystem.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class BookValidator {
    private final BookRepository bookRepository;

    public void validatePublishedYear(Integer publishedYear) {
        if (publishedYear > Year.now().getValue()) {
            throw new InvalidRequestException("Published year cannot be in the future.");
        }
    }

    public void validateUniqueTitle(String title) {
        if (bookRepository.existsByTitle(title)) {
            throw new DuplicateResourceException("Book", "Title", title);
        }
    }

    public void validateUniqueIsbn(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException("Book", "ISBN", isbn);
        }
    }
}
