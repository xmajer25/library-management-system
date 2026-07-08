package com.xmajer.librarymanagementsystem.service;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.exception.EntityNotFoundException;
import com.xmajer.librarymanagementsystem.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public BookDetailResponse getBookById(Long id){
        Book book = bookRepository.findByIdWithCopies(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));

        return bookMapper.toDetailedResponse(book);
    }
}
