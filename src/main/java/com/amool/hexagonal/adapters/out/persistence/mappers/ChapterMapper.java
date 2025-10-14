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

        if (entity.getWorkEntity() != null) {
            chapter.setWorkId(entity.getWorkEntity().getId());
        }

        if (entity.getLanguageEntity() != null) {
            chapter.setLanguageId(entity.getLanguageEntity().getId());
        }

        chapter.setAllowAiTranslation(entity.getAllowAiTranslation());
        chapter.setPublicationStatus(entity.getPublicationStatus());
        chapter.setScheduledPublicationDate(entity.getScheduledPublicationDate());
        chapter.setPublishedAt(entity.getPublishedAt());

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

    public static List<ChapterEntity> toEntityList(List<Chapter> chapters) {
        if (chapters == null) {
            return null;
        }
        return chapters.stream()
                .map(chapter -> {
                    ChapterEntity entity = new ChapterEntity();
                    entity.setId(chapter.getId());
                    entity.setTitle(chapter.getTitle());
                    entity.setPrice(chapter.getPrice());
                    entity.setLikes(chapter.getLikes());
                    entity.setLastModified(chapter.getLastModified());
                    entity.setAllowAiTranslation(chapter.getAllowAiTranslation());
                    entity.setPublicationStatus(chapter.getPublicationStatus());
                    entity.setScheduledPublicationDate(chapter.getScheduledPublicationDate());
                    entity.setPublishedAt(chapter.getPublishedAt());
                    return entity;
                })
                .collect(Collectors.toList());
    }
}
