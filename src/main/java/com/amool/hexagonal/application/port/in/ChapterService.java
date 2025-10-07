package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Chapter;

import java.util.Optional;

public interface ChapterService {
    Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language);

    Chapter createEmptyChapter(Long workId, Long languageId, String contentType);

    record ChapterWithContent(Chapter chapter, String content) {}
}
