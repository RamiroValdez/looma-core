package com.amool.application.port.out;

public interface ReadingProgressPort {

    boolean update(Long userId, Long workId, Long chapterId);

    boolean create(Long userId, Long workId, Long chapterId);
    
}
