package com.xmajer.librarymanagementsystem.mapper;

import com.xmajer.librarymanagementsystem.data.model.BookCopy;
import com.xmajer.librarymanagementsystem.dto.request.AvailabilityUpdateBookCopyRequest;
import com.xmajer.librarymanagementsystem.dto.response.BookCopyResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyResponse toResponse(BookCopy bookCopy);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    void updateEntityFromRequest(
            AvailabilityUpdateBookCopyRequest request,
            @MappingTarget BookCopy bookCopy
    );
}
