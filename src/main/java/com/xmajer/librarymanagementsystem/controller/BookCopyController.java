package com.xmajer.librarymanagementsystem.controller;

import com.xmajer.librarymanagementsystem.data.repository.BookCopyRepository;
import com.xmajer.librarymanagementsystem.service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books/{bookId}/copies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;
}
