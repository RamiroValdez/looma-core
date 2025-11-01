package com.amool.application.usecases;

import com.amool.application.port.out.LikePort;
import com.amool.domain.model.Work;

public class CheckWorkLikesUseCase {
    private final LikePort likePort;

    public CheckWorkLikesUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public void execute(Work work, Long currentUserId) {
        if (currentUserId != null) {
            boolean isLiked = likePort.isWorkLikedByUser(work.getId(), currentUserId);
            work.setLikedByUser(isLiked);
            
            work.getChapters().forEach(chapter -> {
                boolean isChapterLiked = likePort.isChapterLikedByUser(chapter.getId(), currentUserId);
                chapter.setLikedByUser(isChapterLiked);
            });
        }
    }
}
