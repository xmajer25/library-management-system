package com.xmajer.librarymanagementsystem.controller;

import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookDetailResponseWithCopies;
import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerGetTest {
    private static final Long BOOK_ID = 1L;
    private static final Long MISSING_BOOK_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void getBookById_whenBookExists_returnsOkAndBookDetail() throws Exception {
        BookDetailResponse response = createBookDetailResponseWithCopies(BOOK_ID);

        when(bookService.getBookById(BOOK_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/books/{id}", BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.publishedYear").value(2008))
                .andExpect(jsonPath("$.copies.length()").value(2))
                .andExpect(jsonPath("$.copies[0].id").value(10))
                .andExpect(jsonPath("$.copies[0].available").value(true))
                .andExpect(jsonPath("$.copies[1].id").value(11))
                .andExpect(jsonPath("$.copies[1].available").value(false));

        verify(bookService).getBookById(BOOK_ID);
    }

    @Test
    void getBookById_whenIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/books/{id}", -1))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).getBookById(any());
    }

    @Test
    void getBookById_whenBookDoesNotExist_returnsNotFound() throws Exception {
        when(bookService.getBookById(MISSING_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Book", MISSING_BOOK_ID));

        mockMvc.perform(get("/api/books/{id}", MISSING_BOOK_ID))
                .andExpect(status().isNotFound());

        verify(bookService).getBookById(MISSING_BOOK_ID);
    }

    @Test
    void getBooks_withDefaultParameters_usesConfiguredPaginationAndDefaultSorting()
            throws Exception {

        BookResponse response = createBookResponse(BOOK_ID);

        when(bookService.getBooks(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);

                    return new PageImpl<>(
                            List.of(response),
                            pageable,
                            1
                    );
                });

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"));

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(bookService).getBooks(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(
                Sort.by(Sort.Direction.ASC, "title"),
                pageable.getSort()
        );
    }

    @Test
    void getBooks_withCustomPaginationAndSorting_passesParametersToService()
            throws Exception {

        when(bookService.getBooks(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    return Page.empty(pageable);
                });

        mockMvc.perform(
                        get("/api/books")
                                .param("page", "2")
                                .param("size", "5")
                                .param("sort", "publishedYear,desc")
                )
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(bookService).getBooks(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();

        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals(
                Sort.by(Sort.Direction.DESC, "publishedYear"),
                pageable.getSort()
        );
    }

    @Test
    void getBooks_whenRequestedSizeExceedsMaximum_capsPageSizeAtOneHundred()
            throws Exception {

        when(bookService.getBooks(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    return Page.empty(pageable);
                });

        mockMvc.perform(
                        get("/api/books")
                                .param("size", "500")
                )
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(bookService).getBooks(pageableCaptor.capture());

        assertEquals(100, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getBooks_whenNoBooksExist_returnsOkWithEmptyContent() throws Exception {
        when(bookService.getBooks(any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(0);
                    return Page.empty(pageable);
                });

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
