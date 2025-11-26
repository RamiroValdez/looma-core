package com.amool.application.usecases;

import com.amool.application.port.out.DeleteChapterContentPort;
import com.amool.application.port.out.DeleteChapterPort;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.LoadWorkOwnershipPort;
import com.amool.domain.model.Chapter;

import java.util.NoSuchElementException;

public class DeleteChapter {

    private final LoadChapterPort loadChapterPort;
    private final DeleteChapterContentPort deleteChapterContentPort;
    private final DeleteChapterPort deleteChapterPort;
    private final LoadWorkOwnershipPort loadWorkOwnershipPort;

    public DeleteChapter(
            LoadChapterPort loadChapterPort,
            DeleteChapterContentPort deleteChapterContentPort,
            DeleteChapterPort deleteChapterPort,
            LoadWorkOwnershipPort loadWorkOwnershipPort
    ) {
        this.loadChapterPort = loadChapterPort;
        this.deleteChapterContentPort = deleteChapterContentPort;
        this.deleteChapterPort = deleteChapterPort;
        this.loadWorkOwnershipPort = loadWorkOwnershipPort;
    }

    public void execute(Long workId, Long chapterId, Long userId) {
        if (!loadWorkOwnershipPort.isOwner(workId, userId)) {
            throw new SecurityException("El usuario no es propietario de la obra");
        }

        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede eliminar un capítulo en estado DRAFT");
        }

        deleteChapterContentPort.deleteContent(workId.toString(), chapterId.toString());
        deleteChapterPort.deleteChapter(workId, chapterId);
    }
}
