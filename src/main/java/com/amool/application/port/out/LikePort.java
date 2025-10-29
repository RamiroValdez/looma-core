package com.amool.application.port.out;

public interface LikePort {
    int incrementLikes(Long workId);
    
    int decrementLikes(Long workId);
}
