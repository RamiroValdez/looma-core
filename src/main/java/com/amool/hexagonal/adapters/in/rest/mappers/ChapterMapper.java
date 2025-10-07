package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterWithContentDto;
import com.amool.hexagonal.adapters.in.rest.dtos.ChapterDto;
import com.amool.hexagonal.application.port.in.ChapterService;

import java.util.List;

public class ChapterMapper {
    public static ChapterWithContentDto toDto(ChapterService.ChapterWithContent chapterWithContent,
                                              String content,
                                              List<String> availableLanguages) {
        if (chapterWithContent == null || chapterWithContent.chapter() == null) {
            return null;
        }
        return new ChapterWithContentDto(
            chapterWithContent.chapter().getId(),
            chapterWithContent.chapter().getTitle(),
            chapterWithContent.chapter().getPrice(),
            content,
            availableLanguages
        );
    }

    public static ChapterDto toDto(com.amool.hexagonal.domain.model.Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setPrice(chapter.getPrice());
        dto.setLikes(chapter.getLikes());
        dto.setLastModified(chapter.getLastModified());
        dto.setAllowAiTranslation(chapter.getAllowAiTranslation());
        dto.setPublicationStatus(chapter.getPublicationStatus());
        dto.setScheduledPublicationDate(chapter.getScheduledPublicationDate());
        dto.setPublishedAt(chapter.getPublishedAt());
        return dto;
    }

}
