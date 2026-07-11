package com.xmajer.librarymanagementsystem.service;


import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.mapper.BookMapper;
import com.xmajer.librarymanagementsystem.service.validation.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBook;
import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookDetailResponse;
import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookRequest;
import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookResponse;
import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createUpdateBookRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final Long BOOK_ID = 1L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookValidator bookValidator;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookResponse bookResponse;
    private BookDetailResponse bookDetailResponse;

    @BeforeEach
    void setUp() {
        book = createBook(BOOK_ID);
        bookResponse = createBookResponse(BOOK_ID);
        bookDetailResponse = createBookDetailResponse(BOOK_ID);
    }

    @Test
    void getBookById_whenBookExists_returnsBookDetailResponse() {
        when(bookRepository.findByIdWithCopies(1L))
                .thenReturn(Optional.of(book));

        when(bookMapper.toDetailedResponse(book))
                .thenReturn(bookDetailResponse);

        BookDetailResponse result = bookService.getBookById(1L);

        assertEquals(bookDetailResponse, result);

        verify(bookRepository).findByIdWithCopies(1L);
        verify(bookMapper).toDetailedResponse(book);
    }

    @Test
    void getBookById_whenBookDoesNotExist_throwsEntityNotFoundException() {
        when(bookRepository.findByIdWithCopies(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(999L)
        );

        verify(bookRepository).findByIdWithCopies(999L);
        verify(bookMapper, never()).toDetailedResponse(any());
    }

    @Test
    void getBooks_returnsMappedPageOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> bookPage = new PageImpl<>(
                List.of(book),
                pageable,
                1
        );

        when(bookRepository.findAll(pageable))
                .thenReturn(bookPage);

        when(bookMapper.toResponse(book))
                .thenReturn(bookResponse);

        Page<BookResponse> result = bookService.getBooks(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponse, result.getContent().getFirst());

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toResponse(book);
    }

    @Test
    void createBook_withValidRequest_savesAndReturnsCreatedBook() {
        CreateBookRequest request = createBookRequest();

        Book mappedBook = createBook(BOOK_ID);
        Book savedBook = createBook(BOOK_ID);

        when(bookMapper.toEntity(any(CreateBookRequest.class)))
                .thenReturn(mappedBook);

        when(bookRepository.save(mappedBook))
                .thenReturn(savedBook);

        when(bookMapper.toResponse(savedBook))
                .thenReturn(bookResponse);

        BookResponse result = bookService.createBook(request);

        assertEquals(bookResponse, result);

        verify(bookValidator).validateCreate(any(CreateBookRequest.class));

        verify(bookMapper).toEntity(any(CreateBookRequest.class));
        verify(bookRepository).save(mappedBook);
        verify(bookMapper).toResponse(savedBook);
    }

    @Test
    void updateBook_whenBookExists_updatesAndReturnsBook() {
        UpdateBookRequest request = createUpdateBookRequest();

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookMapper.toResponse(book))
                .thenReturn(bookResponse);

        BookResponse result = bookService.updateBook(1L, request);

        assertEquals(bookResponse, result);

        verify(bookRepository).findById(1L);
        verify(bookValidator).validateUpdate(1L, request);
        verify(bookMapper).updateEntityFromRequest(request, book);
        verify(bookRepository).save(book);
        verify(bookMapper).toResponse(book);
    }

    @Test
    void updateBook_whenBookDoesNotExist_throwsEntityNotFoundException() {
        UpdateBookRequest request = createUpdateBookRequest();

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateBook(999L, request)
        );

        verify(bookRepository).findById(999L);
        verify(bookValidator, never()).validateUpdate(any(), any());
        verify(bookMapper, never()).updateEntityFromRequest(any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_whenBookExists_deletesBook() {
        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_whenBookDoesNotExist_throwsEntityNotFoundException() {
        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> bookService.deleteBook(999L)
        );

        verify(bookRepository).findById(999L);
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void createBook_trimsStringFieldsBeforeValidationAndMapping() {
        CreateBookRequest request = new CreateBookRequest(
                "  Clean Code  ",
                "  Robert C. Martin  ",
                "  978-0132350884  ",
                2008
        );

        Book mappedBook = createBook(BOOK_ID);

        when(bookMapper.toEntity(any(CreateBookRequest.class)))
                .thenReturn(mappedBook);

        when(bookRepository.save(mappedBook))
                .thenReturn(mappedBook);

        when(bookMapper.toResponse(mappedBook))
                .thenReturn(bookResponse);

        bookService.createBook(request);

        verify(bookValidator).validateCreate(any(CreateBookRequest.class));

        verify(bookMapper).toEntity(
                new CreateBookRequest(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884",
                        2008
                )
        );
    }

    @Test
    void updateBook_normalizesRequestBeforeValidationAndMapping() {
        Long bookId = 1L;

        Book book = createBook(bookId);

        UpdateBookRequest request = new UpdateBookRequest(
                "  Clean Code Updated  ",
                "  Robert C. Martin  ",
                "  978-0132350884  ",
                2009
        );

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        when(bookRepository.save(book))
                .thenReturn(book);

        when(bookMapper.toResponse(book))
                .thenReturn(createBookResponse(bookId));

        bookService.updateBook(bookId, request);

        ArgumentCaptor<UpdateBookRequest> validatorCaptor =
                ArgumentCaptor.forClass(UpdateBookRequest.class);

        verify(bookValidator).validateUpdate(
                eq(bookId),
                validatorCaptor.capture()
        );

        UpdateBookRequest validatedRequest = validatorCaptor.getValue();

        assertEquals("Clean Code Updated", validatedRequest.title());
        assertEquals("Robert C. Martin", validatedRequest.author());
        assertEquals("978-0132350884", validatedRequest.isbn());
        assertEquals(2009, validatedRequest.publishedYear());
    }
}
