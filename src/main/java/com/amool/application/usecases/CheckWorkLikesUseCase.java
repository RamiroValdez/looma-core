package com.amool.application.usecases;

import com.amool.application.port.out.VerifyUserLikedPort;
import com.amool.domain.model.Work;

public class CheckWorkLikesUseCase {
    private final VerifyUserLikedPort verifyUserLikedPort;

    public CheckWorkLikesUseCase(VerifyUserLikedPort verifyUserLikedPort) {
        this.verifyUserLikedPort = verifyUserLikedPort;
    }

    public void execute(Work work, Long currentUserId) {
        if (currentUserId != null) {
            boolean isLiked = verifyUserLikedPort.isWorkLikedByUser(work.getId(), currentUserId);
            work.setLikedByUser(isLiked);
            
            work.getChapters().forEach(chapter -> {
                boolean isChapterLiked = verifyUserLikedPort.isChapterLikedByUser(chapter.getId(), currentUserId);
                chapter.setLikedByUser(isChapterLiked);
            });
        }
    }
}
