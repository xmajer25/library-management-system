package com.xmajer.librarymanagementsystem.service.validation;

import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.exception.DuplicateResourceException;
import com.xmajer.librarymanagementsystem.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookRequest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookValidatorTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookValidator bookValidator;

    @Test
    void validateCreate_whenTitleAlreadyExists_throwsDuplicateResourceException() {
        CreateBookRequest request = createBookRequest();

        when(bookRepository.existsByTitle(request.title()))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> bookValidator.validateCreate(request)
        );

        verify(bookRepository).existsByTitle(request.title());
        verify(bookRepository, never()).existsByIsbn(request.isbn());
    }

    @Test
    void validateCreate_whenIsbnAlreadyExists_throwsDuplicateResourceException() {
        CreateBookRequest request = createBookRequest();

        when(bookRepository.existsByTitle(request.title()))
                .thenReturn(false);

        when(bookRepository.existsByIsbn(request.isbn()))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> bookValidator.validateCreate(request)
        );

        verify(bookRepository).existsByTitle(request.title());
        verify(bookRepository).existsByIsbn(request.isbn());
    }

    @Test
    void validateCreate_whenPublishedYearIsInFuture_throwsInvalidRequestException() {
        CreateBookRequest request = new CreateBookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                Year.now().getValue() + 1
        );

        assertThrows(
                InvalidRequestException.class,
                () -> bookValidator.validateCreate(request)
        );

        verify(bookRepository, never()).existsByTitle(request.title());
        verify(bookRepository, never()).existsByIsbn(request.isbn());
    }

    @Test
    void validateCreate_whenRequestIsValid_doesNotThrow() {
        CreateBookRequest request = createBookRequest();

        when(bookRepository.existsByTitle(request.title()))
                .thenReturn(false);

        when(bookRepository.existsByIsbn(request.isbn()))
                .thenReturn(false);

        assertDoesNotThrow(() -> bookValidator.validateCreate(request));

        verify(bookRepository).existsByTitle(request.title());
        verify(bookRepository).existsByIsbn(request.isbn());
    }

    @Test
    void validateUpdate_whenTitleAlreadyExistsForAnotherBook_throwsDuplicateResourceException() {
        Long bookId = 1L;

        UpdateBookRequest request = new UpdateBookRequest(
                "Effective Java",
                null,
                null,
                null
        );

        when(bookRepository.existsByTitleAndIdNot(request.title(), bookId))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> bookValidator.validateUpdate(bookId, request)
        );

        verify(bookRepository)
                .existsByTitleAndIdNot(request.title(), bookId);
    }

    @Test
    void validateUpdate_whenIsbnAlreadyExistsForAnotherBook_throwsDuplicateResourceException() {
        Long bookId = 1L;

        UpdateBookRequest request = new UpdateBookRequest(
                null,
                null,
                "978-0134685991",
                null
        );

        when(bookRepository.existsByIsbnAndIdNot(request.isbn(), bookId))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> bookValidator.validateUpdate(bookId, request)
        );

        verify(bookRepository)
                .existsByIsbnAndIdNot(request.isbn(), bookId);
    }

    @Test
    void validateUpdate_whenPublishedYearIsInFuture_throwsInvalidRequestException() {
        Long bookId = 1L;

        UpdateBookRequest request = new UpdateBookRequest(
                null,
                null,
                null,
                Year.now().getValue() + 1
        );

        assertThrows(
                InvalidRequestException.class,
                () -> bookValidator.validateUpdate(bookId, request)
        );

        verify(bookRepository, never())
                .existsByTitleAndIdNot(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong());

        verify(bookRepository, never())
                .existsByIsbnAndIdNot(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void validateUpdate_whenNoFieldsAreProvided_throwsInvalidRequestException() {
        Long bookId = 1L;

        UpdateBookRequest request = new UpdateBookRequest(
                null,
                null,
                null,
                null
        );

        assertThrows(
                InvalidRequestException.class,
                () -> bookValidator.validateUpdate(bookId, request)
        );

        verify(bookRepository, never())
                .existsByTitleAndIdNot(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong());

        verify(bookRepository, never())
                .existsByIsbnAndIdNot(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void validateUpdate_whenAtLeastOneValidFieldIsProvided_doesNotThrow() {
        Long bookId = 1L;

        UpdateBookRequest request = new UpdateBookRequest(
                "Clean Code Updated",
                null,
                null,
                null
        );

        when(bookRepository.existsByTitleAndIdNot(request.title(), bookId))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> bookValidator.validateUpdate(bookId, request)
        );

        verify(bookRepository)
                .existsByTitleAndIdNot(request.title(), bookId);
    }
}