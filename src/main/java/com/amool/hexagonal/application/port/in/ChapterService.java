package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.domain.model.Chapter;

import java.util.Optional;

public interface ChapterService {
    Optional<ChapterWithContent> getChapterWithContent(Long workId, Long chapterId, String language);

    Chapter createEmptyChapter(Long workId, Long languageId, String contentType);
    
    Optional<ChapterResponseDto> getChapterForEdit(Long chapterId, String language);

    void deleteChapter(Long workId, Long chapterId);

    record ChapterWithContent(Chapter chapter, String content) {}
}
