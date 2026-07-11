package com.xmajer.librarymanagementsystem.web;

import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookViewController.class)
class BookViewControllerTest {

    private static final Long MISSING_BOOK_ID = 99L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void getBookDetail_whenBookDoesNotExist_returnsStyledNotFoundPage()
            throws Exception {

        when(bookService.getBookById(MISSING_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Book", MISSING_BOOK_ID));

        mockMvc.perform(get("/books/{id}", MISSING_BOOK_ID))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(content().string(containsString("href=\"/css/base.css\"")))
                .andExpect(content().string(containsString("Page Not Found | The Library")));

        verify(bookService).getBookById(MISSING_BOOK_ID);
    }
}
