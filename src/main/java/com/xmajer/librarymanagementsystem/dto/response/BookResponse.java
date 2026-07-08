package com.xmajer.librarymanagementsystem.dto.response;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publishedYear
){
}
