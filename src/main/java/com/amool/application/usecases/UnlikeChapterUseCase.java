package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

public class UnlikeChapterUseCase {
    
    private final LikePort likePort;

    public UnlikeChapterUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    /*public Long execute(Long chapterId, Long userId) {
        return likePort.unlikeChapter(chapterId, userId);
    }*/

    public LikeResponseDto execute(Long chapterId, Long userId) {
        Long likeCount = likePort.unlikeChapter(chapterId, userId);
        boolean likedByUser = likePort.hasUserLikedChapter(chapterId, userId);
        return new LikeResponseDto(chapterId, likeCount, likedByUser);
    }

}
