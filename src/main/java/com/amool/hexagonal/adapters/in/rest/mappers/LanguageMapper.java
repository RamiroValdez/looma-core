package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.LanguageDto;
import com.amool.hexagonal.domain.model.Language;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageMapper {

    public static LanguageDto toDto(Language language) {
        if (language == null) {
            return null;
        }
        LanguageDto languageDto = new LanguageDto();
        languageDto.setId(language.getId());
        languageDto.setName(language.getName());
        languageDto.setCode(language.getCode());
        return languageDto;
    }

    public static List<LanguageDto> toDtoList(List<Language> languages) {
        if (languages == null) {
            return List.of();
        }
        return languages.stream()
                .map(LanguageMapper::toDto)
                .collect(Collectors.toList());
    }
}
