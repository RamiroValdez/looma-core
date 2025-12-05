package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.UpdateChapterRequest;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.UpdateChapterPort;
import com.amool.domain.model.Chapter;

import java.util.Optional;

public class UpdateChapter {

    private final LoadChapterPort loadChapterPort;
    private final UpdateChapterPort updateChapterPort;
    private final SaveChapterContentPort saveChapterContentPort;

    public UpdateChapter(
            LoadChapterPort loadChapterPort,
            UpdateChapterPort updateChapterPort,
            SaveChapterContentPort saveChapterContentPort
            ){
        this.loadChapterPort = loadChapterPort;
        this.updateChapterPort = updateChapterPort;
        this.saveChapterContentPort = saveChapterContentPort;
    }

    public boolean execute(Long chapterId, UpdateChapterRequest updateRequest) {
        return loadChapterPort.loadChapterForEdit(chapterId)
                .map(existingChapter -> {
                    applyUpdates(existingChapter, updateRequest);
                    Optional<Chapter> updatedChapter = updateChapterPort.updateChapter(existingChapter);
                    updatedChapter.ifPresent(chapter -> updateVersionsIfNeeded(chapter, updateRequest));
                    return updatedChapter.isPresent();
                })
                .orElse(false);
    }

    private void applyUpdates(Chapter chapter, UpdateChapterRequest updateRequest) {
        if (updateRequest.getTitle() != null) {
            chapter.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getStatus() != null) {
            chapter.setPublicationStatus(updateRequest.getStatus());
        }
        if (updateRequest.getPrice() != null) {
            chapter.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getLast_update() != null) {
            chapter.setLastModified(updateRequest.getLast_update());
        }
        if (updateRequest.getAllow_ai_translation() != null) {
            chapter.setAllowAiTranslation(updateRequest.getAllow_ai_translation());
        }
    }

    private void updateVersionsIfNeeded(Chapter savedChapter, UpdateChapterRequest updateRequest) {
        if (updateRequest.getVersions() == null || updateRequest.getVersions().isEmpty()) {
            return;
        }
        String workId = savedChapter.getWorkId() != null ? savedChapter.getWorkId().toString() : null;
        String chapterId = savedChapter.getId() != null ? savedChapter.getId().toString() : null;
        if (workId == null || chapterId == null) {
            return;
        }

        updateRequest.getVersions().forEach((languageCode, content) ->
                saveChapterContentPort.saveContent(workId, chapterId, languageCode, content != null ? content : "")
        );
    }

}
