package com.amool.hexagonal.application.port.out;

import java.util.List;

public interface SubscriptionQueryPort {
    boolean isSubscribedToAuthor(Long userId, Long authorId);
    boolean isSubscribedToWork(Long userId, Long workId);
    List<Long> unlockedChapters(Long userId, Long workId);
}
