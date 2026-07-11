package com.xmajer.librarymanagementsystem.controller;

import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import com.xmajer.librarymanagementsystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Operations for managing books")
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


    @Operation(
            summary = "Create new book",
            description = "Creates a new book without copies and returns the created book."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Book title or ISBN already exists")
    })
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request
    ) {
        BookResponse createdBook = bookService.createBook(request);

        URI location = URI.create("/api/books/" + createdBook.id());

        return ResponseEntity.created(location).body(createdBook);
    }


    @Operation(
            summary = "Update book by ID",
            description = "Partially updates an existing book. Only provided fields are updated."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID or request body"),
            @ApiResponse(responseCode = "404", description = "Book was not found"),
            @ApiResponse(responseCode = "409", description = "Book title or ISBN already exists")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable
            @Positive(message = "Book ID must be positive.")
            Long id,

            @Valid
            @RequestBody
            UpdateBookRequest request
    ) {
        BookResponse updatedBook = bookService.updateBook(id, request);

        return ResponseEntity.ok(updatedBook);
    }


    @Operation(
            summary = "Delete book by ID",
            description = "Deletes a book and its copies."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID"),
            @ApiResponse(responseCode = "404", description = "Book was not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable
            @Positive(message = "Book ID must be positive.")
            Long id
    ) {
        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}
