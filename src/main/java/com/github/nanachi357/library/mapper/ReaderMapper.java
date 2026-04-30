package com.github.nanachi357.library.mapper;

import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.entity.Reader;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReaderMapper {

    ReaderResponse toResponse(Reader reader);

    @Mapping(target = "id", ignore = true)
    Reader toEntity(CreateReaderRequest request);

}
