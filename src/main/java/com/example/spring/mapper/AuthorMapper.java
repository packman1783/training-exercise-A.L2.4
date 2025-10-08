package com.example.spring.mapper;

import com.example.spring.dto.AuthorCreateDTO;
import com.example.spring.dto.AuthorDTO;
import com.example.spring.dto.AuthorUpdateDTO;
import com.example.spring.model.Author;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class AuthorMapper {
    public abstract Author map(AuthorCreateDTO dto);

    public abstract AuthorDTO map(Author model);

    public abstract void update(AuthorUpdateDTO dto, @MappingTarget Author model);
}
