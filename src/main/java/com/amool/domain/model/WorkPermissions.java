package com.amool.domain.model;

import java.util.List;

public class WorkPermissions {

    private final boolean subscribedToAuthor;
    private final boolean subscribedToWork;
    private final List<Long> unlockedChapters;

    private WorkPermissions(boolean subscribedToAuthor, boolean subscribedToWork, List<Long> unlockedChapters) {
        this.subscribedToAuthor = subscribedToAuthor;
        this.subscribedToWork = subscribedToWork;
        this.unlockedChapters = unlockedChapters;
    }

    public static WorkPermissions createAuthor(List<Long> allChapterIds) {
        return new WorkPermissions(true, true, allChapterIds);
    }

    public static WorkPermissions createUser(boolean subscribedToAuthor, boolean subscribedToWork, List<Long> unlockedChapters) {
        return new WorkPermissions(subscribedToAuthor, subscribedToWork, unlockedChapters);
    }

    public static WorkPermissions createGuest() {
        return new WorkPermissions(false, false, List.of());
    }

    public boolean isSubscribedToAuthor() {
        return subscribedToAuthor;
    }

    public boolean isSubscribedToWork() {
        return subscribedToWork;
    }

    public List<Long> getUnlockedChapters() {
        return unlockedChapters;
    }
}
