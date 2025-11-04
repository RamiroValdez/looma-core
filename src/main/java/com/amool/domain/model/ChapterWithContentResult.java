package com.amool.domain.model;

import java.util.List;

public class ChapterWithContentResult {
    private final ChapterWithContent chapterWithContent;
    private final String content;
    private final List<String> availableLanguages;

    public ChapterWithContentResult(ChapterWithContent chapterWithContent, String content, List<String> availableLanguages) {
        this.chapterWithContent = chapterWithContent;
        this.content = content;
        this.availableLanguages = availableLanguages;
    }

    public ChapterWithContent getChapterWithContent() {
        return chapterWithContent;
    }

    public String getContent() {
        return content;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }
}
