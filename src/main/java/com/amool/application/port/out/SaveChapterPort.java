package com.amool.application.port.out;

import com.amool.domain.model.Chapter;

public interface SaveChapterPort {
    Chapter saveChapter(Chapter chapter);
}
