package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.VersionEntity;
import com.amool.hexagonal.domain.model.Version;

import java.util.List;
import java.util.stream.Collectors;

public class VersionMapper {

    public static Version toDomain(VersionEntity entity) {
        if (entity == null) {
            return null;
        }
        Version version = new Version();
        version.setId(entity.getId());
        version.setContent(entity.getContent());

        if (entity.getChapterEntity() != null) {
            version.setChapterId(entity.getChapterEntity().getId());
        }

        if (entity.getLanguageEntity() != null) {
            version.setLanguageId(entity.getLanguageEntity().getId());
        }

        return version;
    }

    public static List<Version> toDomainList(List<VersionEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(VersionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
