package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.application.service.ChapterWithContent;
import java.util.Optional;

public interface ChapterService {
    Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language);
}
