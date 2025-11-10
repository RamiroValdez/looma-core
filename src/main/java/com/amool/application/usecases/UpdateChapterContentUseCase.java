package com.amool.application.usecases;

import com.amool.application.port.out.LoadWorkOwnershipPort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.domain.model.ChapterContent;

public class UpdateChapterContentUseCase {

    private final SaveChapterContentPort saveChapterContentPort;
    private final LoadWorkOwnershipPort loadWorkOwnershipPort;

    public UpdateChapterContentUseCase(
            SaveChapterContentPort saveChapterContentPort,
            LoadWorkOwnershipPort loadWorkOwnershipPort) {
        this.saveChapterContentPort = saveChapterContentPort;
        this.loadWorkOwnershipPort = loadWorkOwnershipPort;
    }

    public ChapterContent execute(String workId, String chapterId, String language,
                                  String content, Long userId) {

        Long workIdLong = Long.valueOf(workId);

        boolean isOwner = loadWorkOwnershipPort.isOwner(workIdLong, userId);
        if (!isOwner) {
            throw new SecurityException("User is not the owner of this work");
        }

        return saveChapterContentPort.saveContent(workId, chapterId, language, content);
    }
}
