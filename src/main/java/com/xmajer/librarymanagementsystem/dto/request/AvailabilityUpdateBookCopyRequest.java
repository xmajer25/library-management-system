package com.xmajer.librarymanagementsystem.dto.request;

import jakarta.validation.constraints.NotNull;

public record AvailabilityUpdateBookCopyRequest(
        @NotNull(message = "Availability status is required.")
        Boolean available
) {
}
