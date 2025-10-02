package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.adapters.in.rest.dto.UpdateChapterContentRequest;
import com.amool.hexagonal.domain.model.ChapterContent;

public interface SaveChapterContentPort {
    ChapterContent saveContent(String workId, String chapterId, String language, String content);
    
    default ChapterContent saveContent(UpdateChapterContentRequest request) {
        return saveContent(request.workId(), request.chapterId(), request.language(), request.content());
    }
}
