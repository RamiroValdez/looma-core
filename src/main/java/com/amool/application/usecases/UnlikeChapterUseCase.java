package com.amool.application.usecases;

import com.amool.application.port.out.LikePort;

public class UnlikeChapterUseCase {
    
    private final LikePort likePort;

    public UnlikeChapterUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public Long execute(Long chapterId, Long userId) {
        return likePort.unlikeChapter(chapterId, userId);
    }
}
