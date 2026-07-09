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

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookCopyResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCopyController.class)
class BookCopyControllerPostTest {

    private static final Long BOOK_ID = 1L;
    private static final Long COPY_ID = 10L;
    private static final Long MISSING_BOOK_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookCopyService bookCopyService;

    @Test
    void addBookCopy_whenBookExists_returnsCreatedCopyAndLocationHeader() throws Exception {
        BookCopyResponse response = createBookCopyResponse(COPY_ID, true);

        when(bookCopyService.addBookCopy(BOOK_ID))
                .thenReturn(response);

        mockMvc.perform(post("/api/books/{bookId}/copies", BOOK_ID))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/books/1/copies/10"
                ))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.available").value(true));

        verify(bookCopyService).addBookCopy(BOOK_ID);
    }

    @Test
    void addBookCopy_whenBookIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/books/{bookId}/copies", -1))
                .andExpect(status().isBadRequest());

        verify(bookCopyService, never()).addBookCopy(any());
    }

    @Test
    void addBookCopy_whenBookDoesNotExist_returnsNotFound() throws Exception {
        when(bookCopyService.addBookCopy(MISSING_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Book", MISSING_BOOK_ID));

        mockMvc.perform(post("/api/books/{bookId}/copies", MISSING_BOOK_ID))
                .andExpect(status().isNotFound());

        verify(bookCopyService).addBookCopy(MISSING_BOOK_ID);
    }
}