package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.FormatEntity;
import com.amool.hexagonal.domain.model.Format;

public class FormatMapper {

    public static Format toDomain(FormatEntity entity) {
        if (entity == null) {
            return null;
        }
        Format format = new Format();
        format.setId(entity.getId());
        format.setName(entity.getName());
        return format;
    }
}
