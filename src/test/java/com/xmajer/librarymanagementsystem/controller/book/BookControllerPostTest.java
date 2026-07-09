package com.xmajer.librarymanagementsystem.controller.book;

import com.xmajer.librarymanagementsystem.controller.BookController;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.exception.DuplicateResourceException;
import com.xmajer.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerPostTest {
    private static final Long BOOK_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
     void createBook_withValidRequest_returnsCreatedBookAndLocationHeader()
            throws Exception {

        BookResponse response = createBookResponse(BOOK_ID);

        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Clean Code",
                                          "author": "Robert C. Martin",
                                          "isbn": "978-0132350884",
                                          "publishedYear": 2008
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/books/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.publishedYear").value(2008));

        verify(bookService).createBook(any(CreateBookRequest.class));
    }

    @Test
    void createBook_withInvalidRequestBody_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "",
                                          "author": "",
                                          "isbn": "",
                                          "publishedYear": null
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(bookService, never()).createBook(any());
    }

    @Test
    void createBook_whenBookIsDuplicate_returnsConflict() throws Exception {
        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenThrow(
                        new DuplicateResourceException(
                                "Book",
                                "Title",
                                "Clean Code"
                        )
                );

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Clean Code",
                                          "author": "Robert C. Martin",
                                          "isbn": "978-0132350884",
                                          "publishedYear": 2008
                                        }
                                        """)
                )
                .andExpect(status().isConflict());

        verify(bookService).createBook(any(CreateBookRequest.class));
    }
}
