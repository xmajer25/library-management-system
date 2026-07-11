package com.xmajer.librarymanagementsystem.integration.api;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookCopyApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @BeforeEach
    void cleanDatabase() {
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void getBookCopies_whenBookHasCopies_returnsAllCopies() throws Exception {
        Book book = createAndSaveBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        BookCopy availableCopy = bookCopyRepository.saveAndFlush(
                new BookCopy(book)
        );

        BookCopy unavailableCopy = new BookCopy(book);
        unavailableCopy.setAvailable(false);
        BookCopy savedUnavailableCopy =
                bookCopyRepository.saveAndFlush(unavailableCopy);

        mockMvc.perform(get("/api/books/{bookId}/copies", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].available").isBoolean())
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].available").isBoolean());

        List<BookCopy> persistedCopies =
                bookCopyRepository.findAllByBookId(book.getId());

        assertEquals(2, persistedCopies.size());
        assertTrue(
                persistedCopies.stream()
                        .anyMatch(copy ->
                                copy.getId().equals(availableCopy.getId())
                                        && copy.getAvailable()
                        )
        );
        assertTrue(
                persistedCopies.stream()
                        .anyMatch(copy ->
                                copy.getId().equals(savedUnavailableCopy.getId())
                                        && !copy.getAvailable()
                        )
        );
    }

    @Test
    void getBookCopies_whenBookHasNoCopies_returnsEmptyList() throws Exception {
        Book book = createAndSaveBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        mockMvc.perform(get("/api/books/{bookId}/copies", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getBookCopies_whenBookDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/{bookId}/copies", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBookCopy_whenBookExists_persistsAvailableCopyAndReturnsLocation()
            throws Exception {

        Book book = createAndSaveBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        MvcResult result = mockMvc.perform(
                        post("/api/books/{bookId}/copies", book.getId())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();

        List<BookCopy> copies =
                bookCopyRepository.findAllByBookId(book.getId());

        assertEquals(1, copies.size());

        BookCopy persistedCopy = copies.getFirst();

        assertTrue(persistedCopy.getAvailable());
        assertEquals(book.getId(), persistedCopy.getBook().getId());

        assertEquals(
                "/api/books/" + book.getId()
                        + "/copies/" + persistedCopy.getId(),
                result.getResponse().getHeader("Location")
        );
    }

    @Test
    void addBookCopy_whenBookDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/books/{bookId}/copies", 999999L))
                .andExpect(status().isNotFound());

        assertEquals(0, bookCopyRepository.count());
    }

    @Test
    void updateAvailability_whenCopyExists_updatesDatabaseAndReturnsResponse()
            throws Exception {

        Book book = createAndSaveBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        BookCopy copy = bookCopyRepository.saveAndFlush(
                new BookCopy(book)
        );

        assertTrue(copy.getAvailable());

        mockMvc.perform(
                        patch(
                                "/api/books/{bookId}/copies/{copyId}",
                                book.getId(),
                                copy.getId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "available": false
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(copy.getId()))
                .andExpect(jsonPath("$.available").value(false));

        BookCopy updatedCopy = bookCopyRepository.findById(copy.getId())
                .orElseThrow();

        assertFalse(updatedCopy.getAvailable());
    }

    @Test
    void updateAvailability_whenCopyBelongsToDifferentBook_returnsNotFound()
            throws Exception {

        Book firstBook = createAndSaveBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        Book secondBook = createAndSaveBook(
                "Effective Java",
                "Joshua Bloch",
                "978-0134685991"
        );

        BookCopy copy = bookCopyRepository.saveAndFlush(
                new BookCopy(firstBook)
        );

        mockMvc.perform(
                        patch(
                                "/api/books/{bookId}/copies/{copyId}",
                                secondBook.getId(),
                                copy.getId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "available": false
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        BookCopy unchangedCopy = bookCopyRepository.findById(copy.getId())
                .orElseThrow();

        assertTrue(unchangedCopy.getAvailable());
    }

    @Test
    void updateAvailability_whenAvailabilityIsMissing_returnsBadRequest()
            throws Exception {

        Book book = createAndSaveBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        BookCopy copy = bookCopyRepository.saveAndFlush(
                new BookCopy(book)
        );

        mockMvc.perform(
                        patch(
                                "/api/books/{bookId}/copies/{copyId}",
                                book.getId(),
                                copy.getId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {}
                                        """)
                )
                .andExpect(status().isBadRequest());

        BookCopy unchangedCopy = bookCopyRepository.findById(copy.getId())
                .orElseThrow();

        assertTrue(unchangedCopy.getAvailable());
    }

    private Book createAndSaveBook(
            String title,
            String author,
            String isbn
    ) {
        return bookRepository.saveAndFlush(
                new Book(title, author, isbn, 2008)
        );
    }
}