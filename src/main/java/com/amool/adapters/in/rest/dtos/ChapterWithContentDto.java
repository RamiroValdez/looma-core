package com.amool.adapters.in.rest.dtos;

import com.amool.domain.model.ChapterWithContent;

import java.util.List;

public record ChapterWithContentDto(
    Long id,
    String title,
    Double price,
    String content,
    List<String> availableLanguages
) {
    public static ChapterWithContentDto from(ChapterWithContent chapterWithContent, List<String> availableLanguages) {
        var chapter = chapterWithContent.chapter();
        return new ChapterWithContentDto(
            chapter.getId(),
            chapter.getTitle(),
            chapter.getPrice(),
            chapterWithContent.content(),
            availableLanguages
        );
    }
}
