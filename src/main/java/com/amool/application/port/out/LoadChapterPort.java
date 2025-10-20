package com.amool.application.port.out;

import com.amool.domain.model.Chapter;
import java.util.Optional;

public interface LoadChapterPort {
    /**
     * Busca un capítulo por su ID y el ID del libro al que pertenece
     * @param workId ID del libro
     * @param chapterId ID del capítulo
     * @return Optional con el capítulo si se encuentra, vacío en caso contrario
     */
    Optional<Chapter> loadChapter(Long workId, Long chapterId);
    Optional<Chapter> loadChapterForEdit(Long chapterId);
}
