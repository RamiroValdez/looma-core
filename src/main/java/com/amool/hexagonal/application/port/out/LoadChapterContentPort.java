package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.ChapterContent;
import java.util.List;
import java.util.Optional;

public interface LoadChapterContentPort {
    Optional<ChapterContent> loadContent(String workId, String chapterId);
    Optional<ChapterContent> loadContent(String workId, String chapterId, String language);
    List<String> getAvailableLanguages(String workId, String chapterId);
}
