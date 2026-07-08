package com.xmajer.librarymanagementsystem.controller;

import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get book detail by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully returned book detail"),
        @ApiResponse(responseCode = "404", description = "Book with this id was not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBookById(@PathVariable Long id){
        BookDetailResponse bookDetailResponse = bookService.getBookById(id);

        return ResponseEntity.ok(bookDetailResponse);
    }
}
