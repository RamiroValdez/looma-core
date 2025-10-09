package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterWithContentDto;
import com.amool.hexagonal.adapters.in.rest.dtos.VersionDto;
import com.amool.hexagonal.adapters.in.rest.dtos.ChapterDto;
import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.domain.model.Chapter;

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

    public static ChapterResponseDto toDto(
            Chapter chapter,
            String content,
            String workName,
            List<String> availableLanguages
            ) {

        if (chapter == null) {
            return null;
        }

        ChapterResponseDto dto = new ChapterResponseDto();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setContent(content); 
        dto.setPrice(chapter.getPrice());
        dto.setWorkName(workName);
        dto.setLastUpdate(chapter.getLastModified());
        dto.setLikes(chapter.getLikes());
        dto.setAllowAiTranslation(chapter.getAllowAiTranslation());
        dto.setLanguageId(chapter.getLanguageId());
        dto.setPublicationStatus(chapter.getPublicationStatus());
        dto.setScheduledPublicationDate(chapter.getScheduledPublicationDate());
        dto.setPublishedAt(chapter.getPublishedAt());
        dto.setAvailableLanguages(availableLanguages);
        return dto;
    }

}
