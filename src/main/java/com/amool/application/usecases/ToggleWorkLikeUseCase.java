package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

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
        
        if (currentlyLiked) {
            // Si ya tiene like, lo quitamos
            likeCount = likePort.unlikeWork(workId, userId);
        } else {
            // Si no tiene like, lo agregamos
            likeCount = likePort.likeWork(workId, userId);
        }
        
        // El estado se invierte después de la operación
        boolean likedByUser = !currentlyLiked;
        return new LikeResponseDto(workId, likeCount, likedByUser);
    }
}
