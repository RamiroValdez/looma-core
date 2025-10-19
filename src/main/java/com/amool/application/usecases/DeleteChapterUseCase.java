package com.amool.application.usecases;

import com.amool.application.port.out.DeleteChapterContentPort;
import com.amool.application.port.out.DeleteChapterPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.domain.model.Chapter;

import java.util.NoSuchElementException;

public class DeleteChapterUseCase {

    private final LoadChapterPort loadChapterPort;
    private final DeleteChapterContentPort deleteChapterContentPort;
    private final DeleteChapterPort deleteChapterPort;

    public DeleteChapterUseCase(
                            LoadChapterPort loadChapterPort,
                            DeleteChapterContentPort deleteChapterContentPort,
                            DeleteChapterPort deleteChapterPort
    ) {
        this.loadChapterPort = loadChapterPort;
        this.deleteChapterContentPort = deleteChapterContentPort;
        this.deleteChapterPort = deleteChapterPort;
    }

    public void execute(Long workId, Long chapterId) {
        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede eliminar un capítulo en estado DRAFT");
        }

        deleteChapterContentPort.deleteContent(workId.toString(), chapterId.toString());
        deleteChapterPort.deleteChapter(workId, chapterId);
    }
}
