package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.FormatDto;
import com.amool.domain.model.Format;
import java.util.List;

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

    public static List<FormatDto> toDtoList(java.util.List<Format> formats) {
        if (formats == null) {
            return null;
        }
        java.util.List<FormatDto> dtoList = new java.util.ArrayList<>();
        for (Format format : formats) {
            dtoList.add(toDto(format));
        }
        return dtoList;
    }
}
