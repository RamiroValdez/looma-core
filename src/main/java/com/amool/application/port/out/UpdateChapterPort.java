package com.amool.application.port.out;

import com.amool.domain.model.Chapter;

import java.util.Optional;

public interface UpdateChapterPort {
    Optional<Chapter> updateChapter(Chapter chapter);
}
