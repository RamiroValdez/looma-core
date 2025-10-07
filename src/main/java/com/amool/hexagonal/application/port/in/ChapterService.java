package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Chapter;

import java.util.Optional;

public interface ChapterService {
    Optional<ChapterWithContent> getChapterWithContent(Long bookId, Long chapterId, String language);

    /**
     * Crea un nuevo capítulo vacío para una obra con idioma específico
     * @param workId ID de la obra
     * @param languageId ID del idioma del capítulo
     * @return Capítulo creado
     */
    Chapter createEmptyChapter(Long workId, Long languageId);

    record ChapterWithContent(Chapter chapter, String content) {}
}
