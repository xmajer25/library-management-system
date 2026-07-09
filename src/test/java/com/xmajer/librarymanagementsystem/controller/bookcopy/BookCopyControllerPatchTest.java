package com.xmajer.librarymanagementsystem.controller.bookcopy;

import com.xmajer.librarymanagementsystem.controller.BookCopyController;
import com.xmajer.librarymanagementsystem.dto.request.AvailabilityUpdateBookCopyRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.service.BookCopyService;
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

@WebMvcTest(BookCopyController.class)
class BookCopyControllerPatchTest {

    private static final Long BOOK_ID = 1L;
    private static final Long COPY_ID = 10L;
    private static final Long MISSING_COPY_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookCopyService bookCopyService;

    @Test
    void updateAvailability_withValidRequest_returnsUpdatedCopy() throws Exception {
        BookCopyResponse response = new BookCopyResponse(COPY_ID, false);

        when(bookCopyService.updateAvailability(
                any(Long.class),
                any(Long.class),
                any(AvailabilityUpdateBookCopyRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        patch("/api/books/{bookId}/copies/{copyId}", BOOK_ID, COPY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "available": false
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.available").value(false));

        verify(bookCopyService).updateAvailability(
                any(Long.class),
                any(Long.class),
                any(AvailabilityUpdateBookCopyRequest.class)
        );
    }

    @Test
    void updateAvailability_whenBookIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        patch("/api/books/{bookId}/copies/{copyId}", -1, COPY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "available": false
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(bookCopyService, never()).updateAvailability(any(), any(), any());
    }

    @Test
    void updateAvailability_whenCopyIdIsNotPositive_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        patch("/api/books/{bookId}/copies/{copyId}", BOOK_ID, -1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "available": false
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(bookCopyService, never()).updateAvailability(any(), any(), any());
    }

    @Test
    void updateAvailability_whenAvailabilityIsMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        patch("/api/books/{bookId}/copies/{copyId}", BOOK_ID, COPY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {}
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(bookCopyService, never()).updateAvailability(any(), any(), any());
    }

    @Test
    void updateAvailability_whenBookCopyDoesNotExist_returnsNotFound() throws Exception {
        when(bookCopyService.updateAvailability(
                any(Long.class),
                any(Long.class),
                any(AvailabilityUpdateBookCopyRequest.class)
        )).thenThrow(
                new EntityNotFoundException("BookCopy", MISSING_COPY_ID)
        );

        mockMvc.perform(
                        patch(
                                "/api/books/{bookId}/copies/{copyId}",
                                BOOK_ID,
                                MISSING_COPY_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "available": false
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        verify(bookCopyService).updateAvailability(
                any(Long.class),
                any(Long.class),
                any(AvailabilityUpdateBookCopyRequest.class)
        );
    }
}