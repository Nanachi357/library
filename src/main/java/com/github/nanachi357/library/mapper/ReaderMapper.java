package com.github.nanachi357.library.mapper;

import com.github.nanachi357.library.dto.CreateReaderRequest;
import com.github.nanachi357.library.dto.PatchReaderRequest;
import com.github.nanachi357.library.dto.ReaderResponse;
import com.github.nanachi357.library.dto.UpdateReaderRequest;
import com.github.nanachi357.library.entity.Reader;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReaderMapper {

    ReaderResponse toResponse(Reader reader);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    Reader toEntity(CreateReaderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateEntity(UpdateReaderRequest request, @MappingTarget Reader reader);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(PatchReaderRequest request, @MappingTarget Reader reader);

}
