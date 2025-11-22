package com.amool.application.port.out;

import java.util.List;

import com.amool.domain.model.Work;

public interface SubscriptionPersistencePort {
    void subscribeChapter(Long userId, Long chapterId);
    void subscribeAuthor(Long userId, Long authorId);
    void subscribeWork(Long userId, Long workId);
    List<Work> getAllSubscribedWorks(Long userId);
}
