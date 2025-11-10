package com.amool.application.port.out;

import java.time.LocalDateTime;
import java.time.Instant;

public interface UpdateChapterStatusPort {

    void updatePublicationStatus(Long workId, Long chapterId, String status, LocalDateTime publishedAt);

    void schedulePublication(Long workId, Long chapterId, Instant when);

    void clearSchedule(Long workId, Long chapterId);
}
