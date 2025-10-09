package com.amool.hexagonal.application.port.out;

public interface DeleteChapterPort {
    void deleteChapter(Long workId, Long chapterId);
}
