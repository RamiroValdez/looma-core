package com.amool.application.port.out;

public interface VerifyUserLikedPort {
    boolean isWorkLikedByUser(Long workId, Long userId);
    boolean isChapterLikedByUser(Long chapterId, Long userId);
}
