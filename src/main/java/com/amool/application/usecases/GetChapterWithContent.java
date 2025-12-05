package com.amool.application.usecases;

import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.domain.model.ChapterWithContent;
import com.amool.domain.model.ChapterContent;
import com.amool.domain.model.ChapterWithContentResult;

import java.util.List;
import java.util.Optional;

public class GetChapterWithContent {

    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;

    public GetChapterWithContent(
            LoadChapterPort loadChapterPort,
            LoadChapterContentPort loadChapterContentPort) {
        this.loadChapterContentPort = loadChapterContentPort;
        this.loadChapterPort = loadChapterPort;
    }

    public Optional<ChapterWithContentResult> execute(Long workId, Long chapterId, String language) {
        return loadChapterPort.loadChapter(workId, chapterId)
                .map(chapter -> {
                    String resolvedContent = resolveContent(workId.toString(), chapterId.toString(), language);
                    List<String> availableLanguages = getAvailableLanguages(workId.toString(), chapterId.toString());

                    return new ChapterWithContentResult(
                            new ChapterWithContent(chapter, resolvedContent),
                            resolvedContent,
                            availableLanguages
                    );
                });
    }

    private String resolveContent(String workId, String chapterId, String language) {
        Optional<ChapterContent> chapterContent = loadChapterContentPort.loadContent(workId, chapterId, language);

        return chapterContent
                .map(ChapterContent::getContentByLanguage)
                .map(contentMap -> getContentForLanguage(contentMap, language))
                .orElse("");
    }

    private String getContentForLanguage(java.util.Map<String, String> contentMap, String language) {
        return contentMap.getOrDefault(language,
                contentMap.values().stream().findFirst().orElse(""));
    }

    private List<String> getAvailableLanguages(String workId, String chapterId) {
        return loadChapterContentPort.getAvailableLanguages(workId, chapterId);
    }
}

