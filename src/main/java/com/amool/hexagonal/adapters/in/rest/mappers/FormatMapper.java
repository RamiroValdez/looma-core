package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.FormatDto;
import com.amool.hexagonal.domain.model.Format;

public class FormatMapper {

    public static FormatDto toDto(Format format) {
        if (format == null) {
            return null;
        }
        FormatDto dto = new FormatDto();
        dto.setId(format.getId());
        dto.setName(format.getName());
        return dto;
    }
}
