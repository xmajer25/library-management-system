package com.xmajer.librarymanagementsystem.controller.book;

import com.xmajer.librarymanagementsystem.controller.BookController;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerDeleteTest {
    private static final Long BOOK_ID = 1L;
    private static final Long MISSING_BOOK_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void deleteBook_whenBookExists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", BOOK_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(bookService).deleteBook(BOOK_ID);
    }

    @Test
    void deleteBook_whenIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", -1))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).deleteBook(any());
    }

    @Test
    void deleteBook_whenBookDoesNotExist_returnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Book", MISSING_BOOK_ID))
                .when(bookService)
                .deleteBook(MISSING_BOOK_ID);

        mockMvc.perform(delete("/api/books/{id}", MISSING_BOOK_ID))
                .andExpect(status().isNotFound());

        verify(bookService).deleteBook(MISSING_BOOK_ID);
    }

}
