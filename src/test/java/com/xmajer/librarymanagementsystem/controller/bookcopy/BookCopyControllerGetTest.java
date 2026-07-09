package com.xmajer.librarymanagementsystem.controller.bookcopy;

import com.xmajer.librarymanagementsystem.controller.BookCopyController;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.service.BookCopyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookCopyResponse;
import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCopyController.class)
class BookCopyControllerGetTest {

    private static final Long BOOK_ID = 1L;
    private static final Long MISSING_BOOK_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookCopyService bookCopyService;

    @Test
    void getBookCopies_whenBookExistsWithCopies_returnsOkAndCopies() throws Exception {
        List<BookCopyResponse> responses = List.of(
                createBookCopyResponse(10L, true),
                createBookCopyResponse(11L, false)
        );

        when(bookCopyService.getBookCopies(BOOK_ID))
                .thenReturn(responses);

        mockMvc.perform(get("/api/books/{bookId}/copies", BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].available").value(false));

        verify(bookCopyService).getBookCopies(BOOK_ID);
    }

    @Test
    void getBookCopies_whenBookExistsWithoutCopies_returnsOkAndEmptyList() throws Exception {
        when(bookCopyService.getBookCopies(BOOK_ID))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/books/{bookId}/copies", BOOK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(bookCopyService).getBookCopies(BOOK_ID);
    }

    @Test
    void getBookCopies_whenBookIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/books/{bookId}/copies", -1))
                .andExpect(status().isBadRequest());

        verify(bookCopyService, never()).getBookCopies(any());
    }

    @Test
    void getBookCopies_whenBookDoesNotExist_returnsNotFound() throws Exception {
        when(bookCopyService.getBookCopies(MISSING_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Book", MISSING_BOOK_ID));

        mockMvc.perform(get("/api/books/{bookId}/copies", MISSING_BOOK_ID))
                .andExpect(status().isNotFound());

        verify(bookCopyService).getBookCopies(MISSING_BOOK_ID);
    }
}