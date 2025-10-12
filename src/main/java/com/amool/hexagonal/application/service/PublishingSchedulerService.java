package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.out.FindChaptersDueForPublicationPort;
import com.amool.hexagonal.application.port.out.UpdateChapterStatusPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class PublishingSchedulerService {

    private final FindChaptersDueForPublicationPort findChaptersDueForPublicationPort;
    private final UpdateChapterStatusPort updateChapterStatusPort;

    public PublishingSchedulerService(FindChaptersDueForPublicationPort findChaptersDueForPublicationPort,
                                      UpdateChapterStatusPort updateChapterStatusPort) {
        this.findChaptersDueForPublicationPort = findChaptersDueForPublicationPort;
        this.updateChapterStatusPort = updateChapterStatusPort;
    }

    public int publishDueChapters(int batchSize) {
        var dueList = findChaptersDueForPublicationPort.findDue(Instant.now(), batchSize);
        int published = 0;
        for (var due : dueList) {
            try {
                updateChapterStatusPort.updatePublicationStatus(
                        due.workId(),
                        due.chapterId(),
                        "PUBLISHED",
                        LocalDateTime.now()
                );
                published++;
            } catch (RuntimeException ex) {
            }
        }
        return published;
    }
}
