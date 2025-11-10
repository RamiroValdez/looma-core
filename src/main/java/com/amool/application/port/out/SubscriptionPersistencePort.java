package com.amool.application.port.out;

public interface SubscriptionPersistencePort {
    void subscribeChapter(Long userId, Long chapterId);
    void subscribeAuthor(Long userId, Long authorId);
    void subscribeWork(Long userId, Long workId);
}
