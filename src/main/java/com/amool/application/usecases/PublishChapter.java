package com.amool.application.usecases;

import com.amool.application.port.out.*;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Work;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class PublishChapter {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterPort loadChapterPort;
    private final UpdateChapterStatusPort updateChapterStatusPort;

    public PublishChapter(
            LoadChapterPort loadChapterPort,
            ObtainWorkByIdPort obtainWorkByIdPort,
            UpdateChapterStatusPort updateChapterStatusPort
            ){
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterPort = loadChapterPort;
        this.updateChapterStatusPort = updateChapterStatusPort;
    }

    public void execute(Long workId, Long chapterId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        Work work = obtainWorkByIdPort.obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));
        if (work.getCreator() == null || !authenticatedUserId.equals(work.getCreator().getId())) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede publicar un capítulo en estado DRAFT");
        }

        updateChapterStatusPort.updatePublicationStatus(workId, chapterId, "PUBLISHED", LocalDateTime.now());
    }

}
