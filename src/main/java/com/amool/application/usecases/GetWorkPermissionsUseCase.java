package com.amool.application.usecases;

import com.amool.application.port.out.SubscriptionQueryPort;
import com.amool.domain.model.Work;
import com.amool.domain.model.WorkPermissions;
import com.amool.domain.model.Chapter;

import java.util.List;
import java.util.stream.Collectors;

public class GetWorkPermissionsUseCase {

    private final SubscriptionQueryPort subscriptionQueryPort;

    public GetWorkPermissionsUseCase(SubscriptionQueryPort subscriptionQueryPort) {
        this.subscriptionQueryPort = subscriptionQueryPort;
    }

    public WorkPermissions execute(Work work, Long userId) {
        if (work == null || userId == null) {
            return WorkPermissions.createGuest();
        }

        Long authorId = work.getCreator() != null ? work.getCreator().getId() : null;

        if (authorId != null && authorId.equals(userId)) {
            List<Long> allChapterIds = work.getChapters().stream()
                    .map(Chapter::getId)
                    .collect(Collectors.toList());

            return WorkPermissions.createAuthor(allChapterIds);
        }

        boolean subscribedToAuthor = authorId != null &&
                subscriptionQueryPort.isSubscribedToAuthor(userId, authorId);
        boolean subscribedToWork = subscriptionQueryPort.isSubscribedToWork(userId, work.getId());
        List<Long> unlockedChapters = subscriptionQueryPort.unlockedChapters(userId, work.getId());

        return WorkPermissions.createUser(subscribedToAuthor, subscribedToWork, unlockedChapters);
    }
}
