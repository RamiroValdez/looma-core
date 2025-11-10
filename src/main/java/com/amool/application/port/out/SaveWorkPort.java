package com.amool.application.port.out;

import com.amool.domain.model.Work;

import java.util.List;

public interface SaveWorkPort {
    void saveWorkForUser(Long userId, Long workId);
    void removeSavedWorkForUser(Long userId, Long workId);
    boolean isWorkSavedByUser(Long userId, Long workId);
    List<Work> getSavedWorksByUser(Long userId);
}
