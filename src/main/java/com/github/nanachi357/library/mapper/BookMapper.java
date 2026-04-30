package com.github.nanachi357.library.mapper;

import com.github.nanachi357.library.dto.BookResponse;
import com.github.nanachi357.library.dto.CreateBookRequest;
import com.github.nanachi357.library.entity.Book;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookResponse toResponse(Book book);

    @Mapping(target = "id", ignore = true)
    Book toEntity(CreateBookRequest request);

}
