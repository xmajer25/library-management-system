package com.xmajer.librarymanagementsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank(message = "Title is required.")
        @Size(max = 255, message = "Title must not exceed 255 characters.")
        String title,

        @NotBlank(message = "Author is required.")
        @Size(max = 255, message = "Author must not exceed 255 characters.")
        String author,

        @NotBlank(message = "ISBN is required.")
        @Pattern(
                regexp = "^(?:97[89][- ]?)?\\d{1,5}[- ]?\\d{1,7}[- ]?\\d{1,7}[- ]?[\\dX]$",
                message = "ISBN must be a valid ISBN-10 or ISBN-13 format."
        )
        String isbn,

        @NotNull(message = "Published year is required.")
        @Min(value = 1000, message = "Published year must be at least 1000.")
        Integer publishedYear
) {
}
