package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.ChapterWithContentDto;
import com.amool.adapters.in.rest.dtos.ChapterDto;
import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.adapters.in.rest.dtos.LanguageDto;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.ChapterWithContent;

import java.util.List;

public class ChapterMapper {
    public static ChapterWithContentDto toDto(ChapterWithContent chapterWithContent,
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

    public static ChapterDto toDto(Chapter chapter) {
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
        dto.setLikedByUser(chapter.getLikedByUser());
        return dto;
    }

    public static ChapterResponseDto toDto(
            Chapter chapter,
            String content,
            String workName,
            String workId,
            List<LanguageDto> availableLanguages,
            Integer chapterNumber,
            LanguageDto languageDefaultCode
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
        dto.setWorkId(workId);
        dto.setLastUpdate(chapter.getLastModified());
        dto.setLikes(chapter.getLikes());
        dto.setAllowAiTranslation(chapter.getAllowAiTranslation());
        dto.setLanguageDefaultCode(languageDefaultCode);
        dto.setPublicationStatus(chapter.getPublicationStatus());
        dto.setScheduledPublicationDate(chapter.getScheduledPublicationDate());
        dto.setPublishedAt(chapter.getPublishedAt());
        dto.setAvailableLanguages(availableLanguages);
        dto.setChapterNumber(chapterNumber);
        return dto;
    }

}
