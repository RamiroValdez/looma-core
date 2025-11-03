package com.amool.application.usecases;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.application.port.out.LikePort;

public class UnlikeWorkUseCase {
    
    private final LikePort likePort;
    
    public UnlikeWorkUseCase(LikePort likePort) {
        this.likePort = likePort;
    }
    
    /*public Long execute(Long workId, Long userId) {
        return likePort.unlikeWork(workId, userId);
    }*/

    public LikeResponseDto execute(Long workId, Long userId) {
        Long likeCount = likePort.unlikeWork(workId, userId);
        boolean likedByUser = likePort.hasUserLikedWork(workId, userId);
        return new LikeResponseDto(workId, likeCount, likedByUser);
    }
}
