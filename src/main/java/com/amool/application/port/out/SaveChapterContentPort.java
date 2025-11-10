package com.amool.application.port.out;

import com.amool.adapters.in.rest.dtos.UpdateChapterContentRequest;
import com.amool.domain.model.ChapterContent;

public interface SaveChapterContentPort {
    ChapterContent saveContent(String workId, String chapterId, String language, String content);
    
    default ChapterContent saveContent(UpdateChapterContentRequest request) {
        return saveContent(request.workId(), request.chapterId(), request.language(), request.content());
    }
}
