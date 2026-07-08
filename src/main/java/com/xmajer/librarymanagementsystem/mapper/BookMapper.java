package com.xmajer.librarymanagementsystem.mapper;

import com.xmajer.librarymanagementsystem.data.model.Book;
import com.xmajer.librarymanagementsystem.dto.request.CreateBookRequest;
import com.xmajer.librarymanagementsystem.dto.request.UpdateBookRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookDetailResponse;
import com.xmajer.librarymanagementsystem.dto.response.BookResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toResponse(Book book);
    BookDetailResponse toDetailedResponse(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "copies", ignore = true)
    Book toEntity(CreateBookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "copies", ignore = true)
    void updateEntityFromRequest(
            UpdateBookRequest request,
            @MappingTarget Book book
    );
}
