package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Chapter;

import java.util.Optional;

public interface ChapterService {
    Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language);
    
    record ChapterWithContent(Chapter chapter, String content) {}
}
