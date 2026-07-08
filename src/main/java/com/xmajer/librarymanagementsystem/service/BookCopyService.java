package com.xmajer.librarymanagementsystem.service;

import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.mapper.BookCopyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookCopyMapper bookCopyMapper;
}
