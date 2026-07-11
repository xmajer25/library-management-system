package com.xmajer.librarymanagementsystem.integration.repository;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.integration.BaseIntegrationTest;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class BookRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Test
    void findByIdWithCopies_whenBookExists_returnsBookWithLoadedCopies() {
        Book book = createBookWithoutId(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        book.addCopy(new BookCopy(book));
        book.addCopy(new BookCopy(book));

        Book savedBook = bookRepository.saveAndFlush(book);

        Optional<Book> result =
                bookRepository.findByIdWithCopies(savedBook.getId());

        assertTrue(result.isPresent());

        Book foundBook = result.get();

        assertEquals(savedBook.getId(), foundBook.getId());
        assertEquals(2, foundBook.getCopies().size());

        assertTrue(
                Persistence.getPersistenceUtil()
                        .isLoaded(foundBook, "copies")
        );
    }

    @Test
    void findByIdWithCopies_whenBookDoesNotExist_returnsEmpty() {
        Optional<Book> result =
                bookRepository.findByIdWithCopies(999999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByTitle_whenTitleExists_returnsTrue() {
        Book book = createBookWithoutId(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        bookRepository.saveAndFlush(book);

        assertTrue(bookRepository.existsByTitle("Clean Code"));
        assertFalse(bookRepository.existsByTitle("Effective Java"));
    }

    @Test
    void existsByIsbn_whenIsbnExists_returnsTrue() {
        Book book = createBookWithoutId(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        bookRepository.saveAndFlush(book);

        assertTrue(bookRepository.existsByIsbn("978-0132350884"));
        assertFalse(bookRepository.existsByIsbn("978-0134685991"));
    }

    @Test
    void existsByTitleAndIdNot_whenTitleBelongsToSameBook_returnsFalse() {
        Book book = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        boolean result = bookRepository.existsByTitleAndIdNot(
                book.getTitle(),
                book.getId()
        );

        assertFalse(result);
    }

    @Test
    void existsByTitleAndIdNot_whenTitleBelongsToDifferentBook_returnsTrue() {
        Book firstBook = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        Book secondBook = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Effective Java",
                        "Joshua Bloch",
                        "978-0134685991"
                )
        );

        boolean result = bookRepository.existsByTitleAndIdNot(
                firstBook.getTitle(),
                secondBook.getId()
        );

        assertTrue(result);
    }

    @Test
    void existsByIsbnAndIdNot_whenIsbnBelongsToSameBook_returnsFalse() {
        Book book = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        boolean result = bookRepository.existsByIsbnAndIdNot(
                book.getIsbn(),
                book.getId()
        );

        assertFalse(result);
    }

    @Test
    void existsByIsbnAndIdNot_whenIsbnBelongsToDifferentBook_returnsTrue() {
        Book firstBook = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        Book secondBook = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Effective Java",
                        "Joshua Bloch",
                        "978-0134685991"
                )
        );

        boolean result = bookRepository.existsByIsbnAndIdNot(
                firstBook.getIsbn(),
                secondBook.getId()
        );

        assertTrue(result);
    }

    @Test
    void save_whenTitleIsDuplicate_throwsDataIntegrityViolationException() {
        Book firstBook = createBookWithoutId(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        Book secondBook = createBookWithoutId(
                "Clean Code",
                "Different Author",
                "978-0134685991"
        );

        bookRepository.saveAndFlush(firstBook);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> bookRepository.saveAndFlush(secondBook)
        );
    }

    @Test
    void save_whenIsbnIsDuplicate_throwsDataIntegrityViolationException() {
        Book firstBook = createBookWithoutId(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        Book secondBook = createBookWithoutId(
                "Different Book",
                "Different Author",
                "978-0132350884"
        );

        bookRepository.saveAndFlush(firstBook);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> bookRepository.saveAndFlush(secondBook)
        );
    }

    @Test
    void delete_whenBookHasCopies_deletesBookAndItsCopies() {
        Book book = createBookWithoutId(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884"
        );

        book.addCopy(new BookCopy(book));
        book.addCopy(new BookCopy(book));

        Book savedBook = bookRepository.saveAndFlush(book);

        assertEquals(2, bookCopyRepository.count());

        bookRepository.delete(savedBook);
        bookRepository.flush();

        assertFalse(bookRepository.existsById(savedBook.getId()));
        assertEquals(0, bookCopyRepository.count());
    }


}