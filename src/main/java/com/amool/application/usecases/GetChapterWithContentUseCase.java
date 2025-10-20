package com.amool.application.usecases;

import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.domain.model.ChapterWithContent;

import java.util.Optional;

public class GetChapterWithContentUseCase {

    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;

    public  GetChapterWithContentUseCase(
            LoadChapterPort loadChapterPort,
            LoadChapterContentPort loadChapterContentPort) {
        this.loadChapterContentPort = loadChapterContentPort;
        this.loadChapterPort = loadChapterPort;
    }

    public Optional<ChapterWithContent> execute(Long workId, Long chapterId, String language) {
        return loadChapterPort.loadChapter(workId, chapterId)
                .flatMap(chapter -> loadChapterContentPort
                        .loadContent(workId.toString(), chapterId.toString(), language)
                        .map(content -> new ChapterWithContent(chapter, content.getContent(language))));
    }

}
