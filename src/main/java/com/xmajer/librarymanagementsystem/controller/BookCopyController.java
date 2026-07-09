package com.xmajer.librarymanagementsystem.controller;

import com.xmajer.librarymanagementsystem.dto.request.AvailabilityUpdateBookCopyRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import com.xmajer.librarymanagementsystem.service.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/copies")
@RequiredArgsConstructor
@Tag(name = "Book Copies", description = "Operations for managing book copies")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @Operation(
            summary = "Get book copies",
            description = "Returns all copies belonging to the specified book."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book copies fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID"),
            @ApiResponse(responseCode = "404", description = "Book was not found")
    })
    @GetMapping
    public ResponseEntity<List<BookCopyResponse>> getBookCopies(
            @PathVariable
            @Positive(message = "Book ID must be positive.")
            Long bookId
    ) {
        List<BookCopyResponse> responses = bookCopyService.getBookCopies(bookId);

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Add book copy",
            description = "Creates a new available copy for the specified book."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book copy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID"),
            @ApiResponse(responseCode = "404", description = "Book was not found")
    })
    @PostMapping
    public ResponseEntity<BookCopyResponse> addBookCopy(
            @PathVariable
            @Positive(message = "Book ID must be positive.")
            Long bookId
    ) {
        BookCopyResponse createdBookCopy = bookCopyService.addBookCopy(bookId);

        URI location = URI.create(
                "/api/books/" + bookId + "/copies/" + createdBookCopy.id()
        );

        return ResponseEntity.created(location).body(createdBookCopy);
    }

    @Operation(
            summary = "Update book copy availability",
            description = "Updates the availability status of a specific book copy."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book copy availability updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request body"),
            @ApiResponse(responseCode = "404", description = "Book copy was not found for the specified book")
    })
    @PatchMapping("/{copyId}")
    public ResponseEntity<BookCopyResponse> updateAvailability(
            @PathVariable
            @Positive(message = "Book ID must be positive.")
            Long bookId,

            @PathVariable
            @Positive(message = "Book copy ID must be positive.")
            Long copyId,

            @Valid
            @RequestBody
            AvailabilityUpdateBookCopyRequest request
    ) {
        BookCopyResponse updatedBookCopy =
                bookCopyService.updateAvailability(bookId, copyId, request);

        return ResponseEntity.ok(updatedBookCopy);
    }
}
