package com.xmajer.librarymanagementsystem.dto.response;

import java.util.List;

public record BookDetailResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear,
        List<BookCopyResponse> copies
) {
}
