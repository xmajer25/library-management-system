package com.xmajer.librarymanagementsystem.service;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.request.AvailabilityUpdateBookCopyRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.mapper.BookCopyMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    private static final Long BOOK_ID = 1L;
    private static final Long COPY_ID = 10L;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookCopyMapper bookCopyMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookCopyService bookCopyService;

    @Test
    void getBookCopies_whenBookExists_returnsMappedCopies() {
        Book book = createBook();

        BookCopy availableCopy = new BookCopy(book);

        BookCopy unavailableCopy = new BookCopy(book);
        unavailableCopy.setAvailable(false);

        BookCopyResponse availableResponse =
                new BookCopyResponse(10L, true);

        BookCopyResponse unavailableResponse =
                new BookCopyResponse(11L, false);

        when(bookRepository.existsById(BOOK_ID))
                .thenReturn(true);

        when(bookCopyRepository.findAllByBookId(BOOK_ID))
                .thenReturn(List.of(availableCopy, unavailableCopy));

        when(bookCopyMapper.toResponse(availableCopy))
                .thenReturn(availableResponse);

        when(bookCopyMapper.toResponse(unavailableCopy))
                .thenReturn(unavailableResponse);

        List<BookCopyResponse> result =
                bookCopyService.getBookCopies(BOOK_ID);

        assertEquals(
                List.of(availableResponse, unavailableResponse),
                result
        );

        verify(bookRepository).existsById(BOOK_ID);
        verify(bookCopyRepository).findAllByBookId(BOOK_ID);
        verify(bookCopyMapper).toResponse(availableCopy);
        verify(bookCopyMapper).toResponse(unavailableCopy);
    }

    @Test
    void getBookCopies_whenBookExistsButHasNoCopies_returnsEmptyList() {
        when(bookRepository.existsById(BOOK_ID))
                .thenReturn(true);

        when(bookCopyRepository.findAllByBookId(BOOK_ID))
                .thenReturn(List.of());

        List<BookCopyResponse> result =
                bookCopyService.getBookCopies(BOOK_ID);

        assertTrue(result.isEmpty());

        verify(bookRepository).existsById(BOOK_ID);
        verify(bookCopyRepository).findAllByBookId(BOOK_ID);
        verify(bookCopyMapper, never()).toResponse(any());
    }

    @Test
    void getBookCopies_whenBookDoesNotExist_throwsEntityNotFoundException() {
        when(bookRepository.existsById(BOOK_ID))
                .thenReturn(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> bookCopyService.getBookCopies(BOOK_ID)
        );

        verify(bookRepository).existsById(BOOK_ID);
        verify(bookCopyRepository, never()).findAllByBookId(any());
        verify(bookCopyMapper, never()).toResponse(any());
    }

    @Test
    void addBookCopy_whenBookExists_savesAndReturnsCreatedCopy() {
        Book book = createBook();

        BookCopy savedBookCopy = new BookCopy(book);

        BookCopyResponse response =
                new BookCopyResponse(COPY_ID, true);

        when(bookRepository.findById(BOOK_ID))
                .thenReturn(Optional.of(book));

        when(bookCopyRepository.save(any(BookCopy.class)))
                .thenReturn(savedBookCopy);

        when(bookCopyMapper.toResponse(savedBookCopy))
                .thenReturn(response);

        BookCopyResponse result =
                bookCopyService.addBookCopy(BOOK_ID);

        assertEquals(response, result);

        ArgumentCaptor<BookCopy> bookCopyCaptor =
                ArgumentCaptor.forClass(BookCopy.class);

        verify(bookCopyRepository).save(bookCopyCaptor.capture());

        BookCopy createdBookCopy = bookCopyCaptor.getValue();

        assertSame(book, createdBookCopy.getBook());
        assertEquals(true, createdBookCopy.getAvailable());

        verify(bookRepository).findById(BOOK_ID);
        verify(bookCopyMapper).toResponse(savedBookCopy);
    }

    @Test
    void addBookCopy_whenBookDoesNotExist_throwsEntityNotFoundException() {
        when(bookRepository.findById(BOOK_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> bookCopyService.addBookCopy(BOOK_ID)
        );

        verify(bookRepository).findById(BOOK_ID);
        verify(bookCopyRepository, never()).save(any());
        verify(bookCopyMapper, never()).toResponse(any());
    }

    @Test
    void updateAvailability_whenCopyExists_updatesSavesAndReturnsCopy() {
        Book book = createBook();

        BookCopy bookCopy = new BookCopy(book);

        AvailabilityUpdateBookCopyRequest request =
                new AvailabilityUpdateBookCopyRequest(false);

        BookCopyResponse response =
                new BookCopyResponse(COPY_ID, false);

        when(bookCopyRepository.findByIdAndBookId(COPY_ID, BOOK_ID))
                .thenReturn(Optional.of(bookCopy));

        when(bookCopyRepository.save(bookCopy))
                .thenReturn(bookCopy);

        when(bookCopyMapper.toResponse(bookCopy))
                .thenReturn(response);

        BookCopyResponse result =
                bookCopyService.updateAvailability(
                        BOOK_ID,
                        COPY_ID,
                        request
                );

        assertEquals(response, result);

        verify(bookCopyRepository)
                .findByIdAndBookId(COPY_ID, BOOK_ID);

        verify(bookCopyMapper)
                .updateEntityFromRequest(request, bookCopy);

        verify(bookCopyRepository).save(bookCopy);
        verify(bookCopyMapper).toResponse(bookCopy);
    }

    @Test
    void updateAvailability_whenCopyDoesNotBelongToBook_throwsEntityNotFoundException() {
        AvailabilityUpdateBookCopyRequest request =
                new AvailabilityUpdateBookCopyRequest(false);

        when(bookCopyRepository.findByIdAndBookId(COPY_ID, BOOK_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> bookCopyService.updateAvailability(
                        BOOK_ID,
                        COPY_ID,
                        request
                )
        );

        verify(bookCopyRepository)
                .findByIdAndBookId(COPY_ID, BOOK_ID);

        verify(bookCopyMapper, never())
                .updateEntityFromRequest(any(), any());

        verify(bookCopyRepository, never()).save(any());
        verify(bookCopyMapper, never()).toResponse(any());
    }
}