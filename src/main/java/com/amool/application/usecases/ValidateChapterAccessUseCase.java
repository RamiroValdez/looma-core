package com.amool.application.usecases;

import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.SubscriptionQueryPort;
import com.amool.domain.model.Chapter;

import java.util.Optional;

public class ValidateChapterAccessUseCase {

    private final LoadChapterPort loadChapterPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final SubscriptionQueryPort subscriptionQueryPort;

    public ValidateChapterAccessUseCase(
            LoadChapterPort loadChapterPort,
            ObtainWorkByIdPort obtainWorkByIdPort,
            SubscriptionQueryPort subscriptionQueryPort) {
        this.loadChapterPort = loadChapterPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.subscriptionQueryPort = subscriptionQueryPort;
    }

    public ChapterAccessResult validateAccess(Long chapterId, Long userId) {
        Optional<Chapter> chapterOpt = loadChapterPort.loadChapterForEdit(chapterId);
        if (chapterOpt.isEmpty()) {
            return ChapterAccessResult.chapterNotFound();
        }

        Chapter chapter = chapterOpt.get();
        Long workId = chapter.getWorkId();
        Long authorId = obtainWorkByIdPort.obtainWorkById(workId)
                .map(w -> w.getCreator() != null ? w.getCreator().getId() : null)
                .orElse(null);

        boolean isOwner = (authorId != null && authorId.equals(userId));
        boolean hasAccess = isOwner
                || (authorId != null && subscriptionQueryPort.isSubscribedToAuthor(userId, authorId))
                || subscriptionQueryPort.isSubscribedToWork(userId, workId)
                || subscriptionQueryPort.unlockedChapters(userId, workId).contains(chapterId);

        return hasAccess ?
                ChapterAccessResult.accessGranted(chapter, workId, authorId, isOwner) :
                ChapterAccessResult.accessDenied();
    }

    public static class ChapterAccessResult {
        private final boolean chapterFound;
        private final boolean accessGranted;
        private final Chapter chapter;
        private final Long workId;
        private final Long authorId;
        private final boolean isOwner;

        private ChapterAccessResult(boolean chapterFound, boolean accessGranted, Chapter chapter,
                                    Long workId, Long authorId, boolean isOwner) {
            this.chapterFound = chapterFound;
            this.accessGranted = accessGranted;
            this.chapter = chapter;
            this.workId = workId;
            this.authorId = authorId;
            this.isOwner = isOwner;
        }

        public static ChapterAccessResult chapterNotFound() {
            return new ChapterAccessResult(false, false, null, null, null, false);
        }

        public static ChapterAccessResult accessDenied() {
            return new ChapterAccessResult(true, false, null, null, null, false);
        }

        public static ChapterAccessResult accessGranted(Chapter chapter, Long workId, Long authorId, boolean isOwner) {
            return new ChapterAccessResult(true, true, chapter, workId, authorId, isOwner);
        }

        public boolean isChapterFound() { return chapterFound; }
        public boolean isAccessGranted() { return accessGranted; }
        public Chapter getChapter() { return chapter; }
        public Long getWorkId() { return workId; }
        public Long getAuthorId() { return authorId; }
        public boolean isOwner() { return isOwner; }
    }
}

