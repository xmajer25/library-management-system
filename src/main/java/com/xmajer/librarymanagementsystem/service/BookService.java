package com.xmajer.librarymanagementsystem.service;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.mapper.BookMapper;
import com.xmajer.librarymanagementsystem.service.validation.BookValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookValidator bookValidator;

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        CreateBookRequest normalizedRequest = new CreateBookRequest(
                request.title().trim(),
                request.author().trim(),
                request.isbn().trim(),
                request.publishedYear()
        );

        bookValidator.validateCreate(normalizedRequest);

        Book book = bookMapper.toEntity(normalizedRequest);
        Book savedBook = bookRepository.save(book);

        log.atInfo()
                .setMessage("Book created successfully")
                .addKeyValue("bookId", savedBook.getId())
                .addKeyValue("title", savedBook.getTitle())
                .addKeyValue("isbn", savedBook.getIsbn())
                .log();

        return bookMapper.toResponse(savedBook);
    }

    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));

        UpdateBookRequest normalizedRequest = new UpdateBookRequest(
                request.title() == null ? null : request.title().trim(),
                request.author() == null ? null : request.author().trim(),
                request.isbn() == null ? null : request.isbn().trim(),
                request.publishedYear()
        );

        bookValidator.validateUpdate(id, normalizedRequest);

        bookMapper.updateEntityFromRequest(normalizedRequest, book);
        Book savedBook = bookRepository.save(book);

        log.atInfo()
                .setMessage("Book updated successfully")
                .addKeyValue("bookId", savedBook.getId())
                .addKeyValue("title", savedBook.getTitle())
                .addKeyValue("isbn", savedBook.getIsbn())
                .log();

        return bookMapper.toResponse(savedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));

        bookRepository.delete(book);

        log.atInfo()
                .setMessage("Book deleted successfully")
                .addKeyValue("bookId", book.getId())
                .addKeyValue("title", book.getTitle())
                .addKeyValue("isbn", book.getIsbn())
                .log();

    }

    @Transactional(readOnly = true)
    public BookDetailResponse getBookById(Long id){
        Book book = bookRepository.findByIdWithCopies(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));

        return bookMapper.toDetailedResponse(book);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(Pageable pageable){
        Page<Book> books = bookRepository.findAll(pageable);

        return books.map(bookMapper::toResponse);
    }
}
