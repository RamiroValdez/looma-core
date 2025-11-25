package com.amool.application.usecases;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.UpdateChapterStatusPort;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.Work;

import java.time.Instant;
import java.util.NoSuchElementException;

public class SchedulePublication {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final LoadChapterPort loadChapterPort;
    private final UpdateChapterStatusPort updateChapterStatusPort;

    public SchedulePublication(
            ObtainWorkByIdPort obtainWorkByIdPort,
            LoadChapterPort loadChapterPort,
            UpdateChapterStatusPort updateChapterStatusPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.loadChapterPort = loadChapterPort;
        this.updateChapterStatusPort = updateChapterStatusPort;
    }

    public void execute(Long workId, Long chapterId, Instant when, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }
        if (when == null || !when.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Fecha/Hora inválida para programar publicación");
        }

        Work work = obtainWorkByIdPort.obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));

        if (work.getCreator() == null || !authenticatedUserId.equals(work.getCreator().getId())) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        Chapter chapter = loadChapterPort.loadChapter(workId, chapterId)
                .orElseThrow(() -> new NoSuchElementException("Capítulo no encontrado"));

        if (!"DRAFT".equals(chapter.getPublicationStatus())) {
            throw new IllegalStateException("Solo se puede programar publicación si el capítulo está en estado DRAFT");
        }

        updateChapterStatusPort.schedulePublication(workId, chapterId, when);
    }

}
