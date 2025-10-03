package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.LanguageDto;
import com.amool.hexagonal.domain.model.Language;

public class LanguageMapper {
    public static LanguageDto toDto(Language language) {
        if (language == null) {
            return null;
        }
        LanguageDto languageDto = new LanguageDto();
        languageDto.setId(language.getId());
        languageDto.setName(language.getName());
        return languageDto;
    }
    
}
