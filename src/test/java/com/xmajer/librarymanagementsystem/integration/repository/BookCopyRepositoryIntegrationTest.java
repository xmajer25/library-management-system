package com.xmajer.librarymanagementsystem.integration.repository;

import com.xmajer.librarymanagementsystem.integration.BaseIntegrationTest;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.xmajer.librarymanagementsystem.support.TestDataFactory.createBookWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class BookCopyRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findAllByBookId_returnsOnlyCopiesBelongingToSpecifiedBook() {
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

        bookCopyRepository.save(new BookCopy(firstBook));
        bookCopyRepository.save(new BookCopy(firstBook));
        bookCopyRepository.save(new BookCopy(secondBook));
        bookCopyRepository.flush();

        List<BookCopy> result =
                bookCopyRepository.findAllByBookId(firstBook.getId());

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(copy ->
                                copy.getBook().getId().equals(firstBook.getId())
                        )
        );
    }

    @Test
    void findAllByBookId_whenBookHasNoCopies_returnsEmptyList() {
        Book book = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        List<BookCopy> result =
                bookCopyRepository.findAllByBookId(book.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndBookId_whenCopyBelongsToBook_returnsCopy() {
        Book book = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        BookCopy savedCopy = bookCopyRepository.saveAndFlush(
                new BookCopy(book)
        );

        Optional<BookCopy> result =
                bookCopyRepository.findByIdAndBookId(
                        savedCopy.getId(),
                        book.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(savedCopy.getId(), result.get().getId());
        assertEquals(book.getId(), result.get().getBook().getId());
    }

    @Test
    void findByIdAndBookId_whenCopyBelongsToDifferentBook_returnsEmpty() {
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

        BookCopy savedCopy = bookCopyRepository.saveAndFlush(
                new BookCopy(firstBook)
        );

        Optional<BookCopy> result =
                bookCopyRepository.findByIdAndBookId(
                        savedCopy.getId(),
                        secondBook.getId()
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void save_persistsBookRelationshipAndDefaultAvailability() {
        Book book = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        BookCopy savedCopy = bookCopyRepository.saveAndFlush(
                new BookCopy(book)
        );

        Optional<BookCopy> result =
                bookCopyRepository.findById(savedCopy.getId());

        assertTrue(result.isPresent());

        BookCopy foundCopy = result.get();

        assertEquals(book.getId(), foundCopy.getBook().getId());
        assertTrue(foundCopy.getAvailable());
    }

    @Test
    void save_whenAvailabilityIsNull_prePersistSetsAvailabilityToTrue() {
        Book book = bookRepository.saveAndFlush(
                createBookWithoutId(
                        "Clean Code",
                        "Robert C. Martin",
                        "978-0132350884"
                )
        );

        BookCopy bookCopy = new BookCopy(book);
        bookCopy.setAvailable(null);

        BookCopy savedCopy = bookCopyRepository.saveAndFlush(bookCopy);

        assertTrue(savedCopy.getAvailable());
    }
}