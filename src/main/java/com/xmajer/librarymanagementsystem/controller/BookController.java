package com.xmajer.librarymanagementsystem.controller;

import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Operations for managing books")
@Validated
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Get book by ID",
            description = "Returns a book with its copies."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned book detail"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID"),
            @ApiResponse(responseCode = "404", description = "Book with this id was not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBookById(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable
            @Positive(message = "Book ID must be positive.")
            Long id
    ) {
        BookDetailResponse bookDetailResponse = bookService.getBookById(id);

        return ResponseEntity.ok(bookDetailResponse);
    }

    @Operation(
            summary = "Get books",
            description = "Returns paginated books with defaults: size: 10, max size: 100, sort: title ascending."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books were fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters")
    })
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(
            @ParameterObject
            @PageableDefault(sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<BookResponse> responses = bookService.getBooks(pageable);

        return ResponseEntity.ok(responses);
    }
}
