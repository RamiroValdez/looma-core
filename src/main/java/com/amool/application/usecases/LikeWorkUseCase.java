package com.amool.application.usecases;

import com.amool.application.port.out.LikePort;

public class LikeWorkUseCase {
    
    private final LikePort likePort;
    
    public LikeWorkUseCase(LikePort likePort) {
        this.likePort = likePort;
    }
    
    public Long execute(Long workId, Long userId) {
        return likePort.likeWork(workId, userId);
    }
}
