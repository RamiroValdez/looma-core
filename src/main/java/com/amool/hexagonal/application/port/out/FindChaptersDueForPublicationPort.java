package com.amool.hexagonal.application.port.out;

import java.time.Instant;
import java.util.List;

public interface FindChaptersDueForPublicationPort {
    record DueChapter(Long workId, Long chapterId) {}
    List<DueChapter> findDue(Instant now, int limit);
}
