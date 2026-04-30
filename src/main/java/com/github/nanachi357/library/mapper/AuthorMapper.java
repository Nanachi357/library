package com.github.nanachi357.library.mapper;

import com.github.nanachi357.library.dto.AuthorResponse;
import com.github.nanachi357.library.dto.CreateAuthorRequest;
import com.github.nanachi357.library.entity.Author;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponse toResponse(Author author);

    @Mapping(target = "id", ignore = true)
    Author toEntity(CreateAuthorRequest request);

}
