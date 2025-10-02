package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.ChapterContent;
import java.util.Optional;

public interface GetChapterUseCase {
    Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language);
    
    record ChapterWithContent(Chapter chapter, String content) {}
}
