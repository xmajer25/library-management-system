package com.xmajer.librarymanagementsystem.service.validation;

import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.exception.DuplicateResourceException;
import com.xmajer.librarymanagementsystem.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class BookValidator {
    private final BookRepository bookRepository;

    public void validateUpdate(Long bookId, UpdateBookRequest request) {
        validateAtLeastOneFieldPresent(request);
        validatePublishedYear(request.publishedYear());

        if (request.title() != null && bookRepository.existsByTitleAndIdNot(request.title(), bookId)) {
            throw new DuplicateResourceException("Book", "Title", request.title());
        }

        if (request.isbn() != null && bookRepository.existsByIsbnAndIdNot(request.isbn(), bookId)) {
            throw new DuplicateResourceException("Book", "ISBN", request.isbn());
        }
    }

    public void validateCreate(CreateBookRequest request){
        validatePublishedYear(request.publishedYear());
        validateUniqueTitle(request.title());
        validateUniqueIsbn(request.isbn());
    }

    private void validatePublishedYear(Integer publishedYear) {
        if (publishedYear != null && publishedYear > Year.now().getValue()) {
            throw new InvalidRequestException("Published year cannot be in the future.");
        }
    }

    private void validateUniqueTitle(String title) {
        if (bookRepository.existsByTitle(title)) {
            throw new DuplicateResourceException("Book", "Title", title);
        }
    }

    private void validateUniqueIsbn(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException("Book", "ISBN", isbn);
        }
    }



    private void validateAtLeastOneFieldPresent(UpdateBookRequest request) {
        if (
                request.title() == null &&
                request.author() == null &&
                request.isbn() == null &&
                request.publishedYear() == null
        ) {
            throw new InvalidRequestException("At least one field must be provided for update.");
        }
    }
}
