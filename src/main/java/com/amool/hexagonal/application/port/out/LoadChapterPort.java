package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Chapter;
import java.util.Optional;

public interface LoadChapterPort {
    /**
     * Busca un capítulo por su ID y el ID del libro al que pertenece
     * @param workId ID del libro
     * @param chapterId ID del capítulo
     * @return Optional con el capítulo si se encuentra, vacío en caso contrario
     */
    Optional<Chapter> loadChapter(Long workId, Long chapterId);
}
