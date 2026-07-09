package com.xmajer.librarymanagementsystem.controller.book;

import com.xmajer.librarymanagementsystem.controller.BookController;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.exception.DuplicateResourceException;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerPatchTest {
    private static final Long BOOK_ID = 1L;
    private static final Long MISSING_BOOK_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void updateBook_withValidRequest_returnsUpdatedBook() throws Exception {
        BookResponse updatedResponse = new BookResponse(
                BOOK_ID,
                "Clean Code - Updated",
                "Robert C. Martin",
                "978-0132350884",
                2008
        );

        when(bookService.updateBook(
                any(Long.class),
                any(UpdateBookRequest.class)
        )).thenReturn(updatedResponse);

        mockMvc.perform(
                        patch("/api/books/{id}", BOOK_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Clean Code - Updated"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code - Updated"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.publishedYear").value(2008));

        verify(bookService).updateBook(
                any(Long.class),
                any(UpdateBookRequest.class)
        );
    }

    @Test
    void updateBook_whenIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        patch("/api/books/{id}", -1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Updated title"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(bookService, never()).updateBook(any(), any());
    }

    @Test
    void updateBook_withInvalidRequestBody_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        patch("/api/books/{id}", BOOK_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "   "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(bookService, never()).updateBook(any(), any());
    }

    @Test
    void updateBook_whenBookDoesNotExist_returnsNotFound() throws Exception {
        when(bookService.updateBook(
                any(Long.class),
                any(UpdateBookRequest.class)
        )).thenThrow(
                new EntityNotFoundException("Book", MISSING_BOOK_ID)
        );

        mockMvc.perform(
                        patch("/api/books/{id}", MISSING_BOOK_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Updated title"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        verify(bookService).updateBook(
                any(Long.class),
                any(UpdateBookRequest.class)
        );
    }

    @Test
    void updateBook_whenTitleOrIsbnIsDuplicate_returnsConflict() throws Exception {
        when(bookService.updateBook(
                any(Long.class),
                any(UpdateBookRequest.class)
        )).thenThrow(
                new DuplicateResourceException(
                        "Book",
                        "Title",
                        "Clean Code"
                )
        );

        mockMvc.perform(
                        patch("/api/books/{id}", BOOK_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Clean Code"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());

        verify(bookService).updateBook(
                any(Long.class),
                any(UpdateBookRequest.class)
        );
    }
}
