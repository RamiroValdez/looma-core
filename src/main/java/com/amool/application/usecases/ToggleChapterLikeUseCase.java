package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

public class ToggleChapterLikeUseCase {
    private final LikePort likePort;

    public ToggleChapterLikeUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public LikeResponseDto execute(Long chapterId, Long userId) {
        boolean isCurrentlyLiked = likePort.hasUserLikedChapter(chapterId, userId);
        Long likeCount;
        
        if (isCurrentlyLiked) {
            likeCount = likePort.unlikeChapter(chapterId, userId);
        } else {
            likeCount = likePort.likeChapter(chapterId, userId);
        }
        
        boolean newLikeStatus = !isCurrentlyLiked;
        return new LikeResponseDto(chapterId, likeCount, newLikeStatus);
    }
}
