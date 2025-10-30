package com.amool.application.port.out;

public interface LikePort {
    Long likeWork(Long workId, Long userId);
    
    Long unlikeWork(Long workId, Long userId);
    
    boolean hasUserLikedWork(Long workId, Long userId);

    Long likeChapter(Long chapterId, Long userId);

    Long unlikeChapter(Long chapterId, Long userId);

    boolean hasUserLikedChapter(Long chapterId, Long userId);
}
