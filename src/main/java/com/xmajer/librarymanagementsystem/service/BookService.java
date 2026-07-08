package com.xmajer.librarymanagementsystem.service;

import com.xmajer.librarymanagementsystem.data.repository.BookRepository;
import com.xmajer.librarymanagementsystem.mapper.BookCopyMapper;
import com.xmajer.librarymanagementsystem.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
}
