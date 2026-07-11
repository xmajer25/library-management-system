package com.xmajer.librarymanagementsystem.support;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Book createBookWithoutId(
            String title,
            String author,
            String isbn
    ) {
        return new Book(
                title,
                author,
                isbn,
                2008
        );
    }

    public static Book createBook(Long id) {
        return createBook(
                id,
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008
        );
    }

    public static Book createBook(
            Long id,
            String title,
            String author,
            String isbn,
            Integer publishedYear
    ) {
        Book book = new Book(title, author, isbn, publishedYear);

        ReflectionTestUtils.setField(book, "id", id);

        return book;
    }

    public static CreateBookRequest createBookRequest() {
        return new CreateBookRequest(
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008
        );
    }

    public static BookCopyResponse createBookCopyResponse(Long id, boolean available) {
        return new BookCopyResponse(id, available);
    }

    public static BookResponse createBookResponse(Long id) {
        return new BookResponse(
                id,
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008
        );
    }

    public static BookDetailResponse createBookDetailResponse(Long id) {
        return new BookDetailResponse(
                id,
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                List.of()
        );
    }

    public static BookDetailResponse createBookDetailResponseWithCopies(Long id) {
        return new BookDetailResponse(
                id,
                "Clean Code",
                "Robert C. Martin",
                "978-0132350884",
                2008,
                List.of(
                        new BookCopyResponse(10L, true),
                        new BookCopyResponse(11L, false)
                )
        );
    }

    public static UpdateBookRequest createUpdateBookRequest() {
        return new UpdateBookRequest(
                "Clean Code - Updated",
                null,
                null,
                2009
        );
    }
}