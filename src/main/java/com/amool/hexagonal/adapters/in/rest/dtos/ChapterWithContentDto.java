package com.amool.hexagonal.adapters.in.rest.dtos;

import com.amool.hexagonal.application.port.in.GetChapterUseCase.ChapterWithContent;
import com.amool.hexagonal.domain.model.Chapter;

import java.util.List;

public record ChapterWithContentDto(
    Long id,
    String title,
    String description,
    Double price,
    String content,
    List<String> availableLanguages
) {
    public static ChapterWithContentDto from(ChapterWithContent chapterWithContent, List<String> availableLanguages) {
        var chapter = chapterWithContent.chapter();
        return new ChapterWithContentDto(
            chapter.getId(),
            chapter.getTitle(),
            chapter.getDescription(),
            chapter.getPrice(),
            chapterWithContent.content(),
            availableLanguages
        );
    }
}
