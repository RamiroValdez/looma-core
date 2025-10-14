package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Chapter;

import java.util.Optional;

public interface UpdateChapterPort {
    Optional<Chapter> updateChapter(Chapter chapter);
}
