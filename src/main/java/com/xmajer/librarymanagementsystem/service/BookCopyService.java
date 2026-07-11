package com.xmajer.librarymanagementsystem.service;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.request.AvailabilityUpdateBookCopyRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.mapper.BookCopyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookCopyMapper bookCopyMapper;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookCopyResponse> getBookCopies(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book", bookId);
        }

        return bookCopyRepository.findAllByBookId(bookId)
                .stream()
                .map(bookCopyMapper::toResponse)
                .toList();
    }

    @Transactional
    public BookCopyResponse addBookCopy(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));

        BookCopy bookCopy = new BookCopy(book);
        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        log.atInfo()
                .setMessage("Book copy successfully added")
                .addKeyValue("bookCopyId", savedBookCopy.getId())
                .addKeyValue("bookId", savedBookCopy.getBook().getId())
                .log();


        return bookCopyMapper.toResponse(savedBookCopy);
    }

    @Transactional
    public BookCopyResponse updateAvailability(
            Long bookId,
            Long copyId,
            AvailabilityUpdateBookCopyRequest request
    ) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndBookId(copyId, bookId)
                .orElseThrow(() -> new EntityNotFoundException("BookCopy", copyId));

        bookCopyMapper.updateEntityFromRequest(request, bookCopy);
        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        log.atInfo()
                .setMessage("Book copy availability successfully updated")
                .addKeyValue("bookCopyId", savedBookCopy.getId())
                .addKeyValue("bookId", savedBookCopy.getBook().getId())
                .addKeyValue("available", savedBookCopy.getAvailable())
                .log();

        return bookCopyMapper.toResponse(savedBookCopy);
    }
}
