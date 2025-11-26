package com.amool.application.usecases;

import java.time.LocalDateTime;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

public class ToggleChapterLike {
    private final LikePort likePort;

    public ToggleChapterLike(LikePort likePort) {
        this.likePort = likePort;
    }

    public LikeResponseDto execute(Long chapterId, Long userId) {
        boolean isCurrentlyLiked = likePort.hasUserLikedChapter(chapterId, userId);
        Long likeCount;
        
        LocalDateTime likedAt = LocalDateTime.now();

        if (isCurrentlyLiked) {
            likeCount = likePort.unlikeChapter(chapterId, userId);
        } else {
            likeCount = likePort.likeChapter(chapterId, userId, likedAt);
        }
        
        boolean newLikeStatus = !isCurrentlyLiked;
        return new LikeResponseDto(chapterId, likeCount, newLikeStatus);
    }

    // Convencion de nombres para DTOs -
}
