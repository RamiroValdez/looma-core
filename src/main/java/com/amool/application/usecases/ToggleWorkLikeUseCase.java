package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;
import java.time.LocalDateTime;

public class ToggleWorkLikeUseCase {
    
    private final LikePort likePort;
    
    public ToggleWorkLikeUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public LikeResponseDto execute(Long workId, Long userId) {
        if (workId == null) {
            throw new IllegalArgumentException("Work ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        boolean currentlyLiked = likePort.hasUserLikedWork(workId, userId);
        Long likeCount;
        
        LocalDateTime likedAt = LocalDateTime.now();
        if (currentlyLiked) {
            likeCount = likePort.unlikeWork(workId, userId);
        } else {
            likeCount = likePort.likeWork(workId, userId, likedAt);
        }
        
        boolean likedByUser = !currentlyLiked;
        return new LikeResponseDto(workId, likeCount, likedByUser);
    }
}
