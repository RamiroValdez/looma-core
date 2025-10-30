package com.amool.application.usecases;

import com.amool.application.port.out.LikePort;

public class LikeChapterUseCase {
    private final LikePort likePort;

    public LikeChapterUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public Long execute(Long chapterId, Long userId) {
        return likePort.likeChapter(chapterId, userId);
    }
}
