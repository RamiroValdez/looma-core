package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.ChapterEntity;
import com.amool.hexagonal.domain.model.Chapter;

import java.util.List;
import java.util.stream.Collectors;

public class ChapterMapper {

    public static Chapter toDomain(ChapterEntity entity) {
        if (entity == null) {
            return null;
        }
        Chapter chapter = new Chapter();
        chapter.setId(entity.getId());
        chapter.setTitle(entity.getTitle());
        chapter.setPrice(entity.getPrice());
        chapter.setLikes(entity.getLikes());
        chapter.setLastModified(entity.getLastModified());
        return chapter;
    }

    public static List<Chapter> toDomainList(List<ChapterEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ChapterMapper::toDomain)
                .collect(Collectors.toList());
    }
}
