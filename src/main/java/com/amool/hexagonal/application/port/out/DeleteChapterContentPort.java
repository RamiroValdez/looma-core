package com.amool.hexagonal.application.port.out;

public interface DeleteChapterContentPort {
    void deleteContent(String workId, String chapterId);
}
