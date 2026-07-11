package com.xmajer.librarymanagementsystem.integration.api;

import com.xmajer.librarymanagementsystem.integration.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookApiIntegrationTest extends BaseIntegrationTest {

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
    void createBook_withValidRequest_persistsBookAndReturnsCreatedResponse() throws Exception {
        MvcResult result = mockMvc.perform(
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
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.publishedYear").value(2008))
                .andReturn();

        Book persistedBook = bookRepository.findAll().getFirst();

        assertEquals(1, bookRepository.count());
        assertEquals("Clean Code", persistedBook.getTitle());
        assertEquals("Robert C. Martin", persistedBook.getAuthor());
        assertEquals("978-0132350884", persistedBook.getIsbn());
        assertEquals(2008, persistedBook.getPublishedYear());

        assertEquals(
                "/api/books/" + persistedBook.getId(),
                result.getResponse().getHeader("Location")
        );
    }

    @Test
    void createBook_withWhitespace_normalizesValuesBeforePersisting() throws Exception {
        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "  Clean Code  ",
                                          "author": "  Robert C. Martin  ",
                                          "isbn": "978-0132350884",
                                          "publishedYear": 2008
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"));

        Book persistedBook = bookRepository.findAll().getFirst();

        assertEquals("Clean Code", persistedBook.getTitle());
        assertEquals("Robert C. Martin", persistedBook.getAuthor());
    }

    @Test
    void createBook_whenTitleAlreadyExists_returnsConflictAndDoesNotCreateBook()
            throws Exception {

        bookRepository.saveAndFlush(
                createBook(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884",
                        2008
                )
        );

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Clean Code",
                                          "author": "Different Author",
                                          "isbn": "978-0134685991",
                                          "publishedYear": 2018
                                        }
                                        """)
                )
                .andExpect(status().isConflict());

        assertEquals(1, bookRepository.count());
    }

    @Test
    void createBook_withInvalidRequest_returnsBadRequestAndDoesNotPersistBook()
            throws Exception {

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "",
                                          "author": "",
                                          "isbn": "invalid-isbn",
                                          "publishedYear": null
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        assertEquals(0, bookRepository.count());
    }

    @Test
    void getBookById_whenBookExists_returnsBookWithCopies() throws Exception {
        Book book = createBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008
        );

        BookCopy availableCopy = new BookCopy(book);

        BookCopy unavailableCopy = new BookCopy(book);
        unavailableCopy.setAvailable(false);

        book.addCopy(availableCopy);
        book.addCopy(unavailableCopy);

        Book savedBook = bookRepository.saveAndFlush(book);

        mockMvc.perform(get("/api/books/{id}", savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.publishedYear").value(2008))
                .andExpect(jsonPath("$.copies.length()").value(2));
    }

    @Test
    void getBookById_whenBookDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooks_returnsPaginatedBooksSortedByTitleAscending() throws Exception {
        bookRepository.saveAndFlush(
                createBook(
                        "Zeta Book",
                        "Author Z",
                        "978-0132350884",
                        2008
                )
        );

        bookRepository.saveAndFlush(
                createBook(
                        "Alpha Book",
                        "Author A",
                        "978-0134685991",
                        2018
                )
        );

        mockMvc.perform(
                        get("/api/books")
                                .param("page", "0")
                                .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Alpha Book"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void updateBook_withPartialRequest_updatesOnlyProvidedFields() throws Exception {
        Book savedBook = bookRepository.saveAndFlush(
                createBook(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884",
                        2008
                )
        );

        mockMvc.perform(
                        patch("/api/books/{id}", savedBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "Clean Code Updated"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.publishedYear").value(2008));

        Book updatedBook = bookRepository.findById(savedBook.getId())
                .orElseThrow();

        assertEquals("Clean Code Updated", updatedBook.getTitle());
        assertEquals("Robert C. Martin", updatedBook.getAuthor());
        assertEquals("978-0132350884", updatedBook.getIsbn());
        assertEquals(2008, updatedBook.getPublishedYear());
    }

    @Test
    void deleteBook_whenBookHasCopies_deletesBookAndItsCopies() throws Exception {
        Book book = createBook(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008
        );

        book.addCopy(new BookCopy(book));
        book.addCopy(new BookCopy(book));

        Book savedBook = bookRepository.saveAndFlush(book);

        Long bookId = savedBook.getId();

        Long firstCopyId = savedBook.getCopies().get(0).getId();
        Long secondCopyId = savedBook.getCopies().get(1).getId();

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(bookRepository.existsById(bookId));
        assertFalse(bookCopyRepository.existsById(firstCopyId));
        assertFalse(bookCopyRepository.existsById(secondCopyId));
    }

    @Test
    void deleteBook_whenBookDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 999999L))
                .andExpect(status().isNotFound());

        assertTrue(bookRepository.findAll().isEmpty());
    }

    private Book createBook(
            String title,
            String author,
            String isbn,
            Integer publishedYear
    ) {
        return new Book(title, author, isbn, publishedYear);
    }
}