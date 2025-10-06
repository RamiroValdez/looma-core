package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.VersionDto;
import com.amool.hexagonal.domain.model.Version;

public class VersionMapper {

    public static VersionDto toDto(Version version) {
        if (version == null) {
            return null;
        }
        VersionDto dto = new VersionDto();
        dto.setId(version.getId());
        dto.setContent(version.getContent());
        dto.setChapterId(version.getChapterId());
        dto.setLanguageId(version.getLanguageId());
        return dto;
    }
}
