package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.GetChapterUseCase;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.LoadChapterPort;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.ChapterContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetChapterService implements GetChapterUseCase {

    private final LoadChapterPort loadChapterPort;
    private final LoadChapterContentPort loadChapterContentPort;

    public GetChapterService(LoadChapterPort loadChapterPort, 
                           LoadChapterContentPort loadChapterContentPort) {
        this.loadChapterPort = loadChapterPort;
        this.loadChapterContentPort = loadChapterContentPort;
    }

    @Override
    public Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language) {
        return loadChapterPort.loadChapter(bookId, chapterId)
                .flatMap(chapter -> {
                    return loadChapterContentPort.loadContent(bookId.toString(), chapterId.toString(), language)
                            .map(content -> new ChapterWithContent(chapter, content.getContent(language)));
                });
    }
}
