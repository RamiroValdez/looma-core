package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

public class LikeWorkUseCase {
    
    private final LikePort likePort;
    
    public LikeWorkUseCase(LikePort likePort) {
        this.likePort = likePort;
    }

    public LikeResponseDto execute(Long workId, Long userId) {
        Long likeCount = likePort.likeWork(workId, userId);
        boolean likedByUser = likePort.hasUserLikedWork(workId, userId);
        return new LikeResponseDto(workId, likeCount, likedByUser);
    }
}
