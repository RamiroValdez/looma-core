package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

public class LikeChapterUseCase {
    private final LikePort likePort;

    public LikeChapterUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public LikeResponseDto execute(Long chapterId, Long userId) {
        Long likeCount = likePort.likeChapter(chapterId, userId);
        boolean likedByUser = likePort.hasUserLikedChapter(chapterId, userId);
        return new LikeResponseDto(chapterId, likeCount, likedByUser);
    }
}
