package com.amool.application.port.out;

import java.time.LocalDateTime;

public interface LikePort {
    Long likeWork(Long workId, Long userId, LocalDateTime likedAt);
    
    Long unlikeWork(Long workId, Long userId);
    
    boolean hasUserLikedWork(Long workId, Long userId);

    Long likeChapter(Long chapterId, Long userId, LocalDateTime likedAt);

    Long unlikeChapter(Long chapterId, Long userId);

    boolean hasUserLikedChapter(Long chapterId, Long userId);

    boolean isWorkLikedByUser(Long workId, Long userId);
    
    boolean isChapterLikedByUser(Long chapterId, Long userId);
}
