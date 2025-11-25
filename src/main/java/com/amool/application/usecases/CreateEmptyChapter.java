package com.amool.application.usecases;

import com.amool.application.port.out.LoadLanguagePort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.SaveChapterPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Language;

public class CreateEmptyChapter {

    private final LoadLanguagePort loadLanguagePort;
    private final SaveChapterPort saveChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;

    public CreateEmptyChapter(
            LoadLanguagePort loadLanguagePort,
            SaveChapterPort saveChapterPort,
            SaveChapterContentPort saveChapterContentPort) {
        this.loadLanguagePort = loadLanguagePort;
        this.saveChapterPort = saveChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
    }

    public Chapter execute(Long workId, Long languageId, String contentType) {

        Chapter chapter = new Chapter();
        chapter.setWorkId(workId);
        chapter.setLanguageId(languageId);

        Chapter savedChapter = saveChapterPort.saveChapter(chapter);

        if ("TEXT".equals(contentType)) {

            String languageCode = this.loadLanguagePort.loadLanguageById(languageId)
                    .map(Language::getCode)
                    .orElse("es");

            String emptyContent = "";
            saveChapterContentPort.saveContent(
                    workId.toString(),
                    savedChapter.getId().toString(),
                    languageCode,
                    emptyContent
            );
        }

        return savedChapter;
    }

}
