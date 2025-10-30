package com.amool.application.port.out;

public interface LikePort {
    int likeWork(Long workId, Long userId);
    
    int unlikeWork(Long workId, Long userId);
    
    boolean hasUserLikedWork(Long workId, Long userId);
}
