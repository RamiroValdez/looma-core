package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.domain.model.Language;

public class LanguageMapper {

    public static Language toDomain(LanguageEntity entity) {
        if (entity == null) {
            return null;
        }
        Language language = new Language();
        language.setId(entity.getId());
        language.setName(entity.getName());
        return language;
    }

    public static LanguageEntity toEntity(Language language) {
        if (language == null) {
            return null;
        }
        LanguageEntity entity = new LanguageEntity();
        entity.setId(language.getId());
        entity.setName(language.getName());
        return entity;
    }
    
}
