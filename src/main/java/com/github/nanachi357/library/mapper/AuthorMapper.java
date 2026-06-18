package com.github.nanachi357.library.mapper;

import com.github.nanachi357.library.dto.PatchAuthorRequest;
import com.github.nanachi357.library.dto.AuthorResponse;
import com.github.nanachi357.library.dto.CreateAuthorRequest;
import com.github.nanachi357.library.dto.UpdateAuthorRequest;
import com.github.nanachi357.library.entity.Author;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponse toResponse(Author author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    Author toEntity(CreateAuthorRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateEntity(UpdateAuthorRequest request, @MappingTarget Author author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(PatchAuthorRequest request, @MappingTarget Author author);

}
