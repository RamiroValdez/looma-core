package com.amool.application.usecases;

import com.amool.application.port.out.LikePort;

public class LikeWorkUseCase {
    
    private final LikePort likePort;
    
    public LikeWorkUseCase(LikePort likePort) {
        this.likePort = likePort;
    }
    
    public int execute(Long workId) {
        return likePort.incrementLikes(workId);
    }
}
