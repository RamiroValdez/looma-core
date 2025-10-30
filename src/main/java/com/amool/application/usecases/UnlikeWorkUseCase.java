package com.amool.application.usecases;

import com.amool.application.port.out.LikePort;

public class UnlikeWorkUseCase {
    
    private final LikePort likePort;
    
    public UnlikeWorkUseCase(LikePort likePort) {
        this.likePort = likePort;
    }
    
    public int execute(Long workId, Long userId) {
        return likePort.unlikeWork(workId, userId);
    }
}
