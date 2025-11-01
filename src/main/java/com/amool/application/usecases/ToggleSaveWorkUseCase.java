package com.amool.application.usecases;

import com.amool.application.port.out.SaveWorkPort;

public class ToggleSaveWorkUseCase {

    private final SaveWorkPort saveWorkPort;

    public ToggleSaveWorkUseCase(SaveWorkPort saveWorkPort) {
        this.saveWorkPort = saveWorkPort;
    }

    public void execute(Long userId, Long workId) {
        if (isWorkSaved(userId, workId)) {
            saveWorkPort.removeSavedWorkForUser(userId, workId);
        } else {
            saveWorkPort.saveWorkForUser(userId, workId);
        }
    }
    
    private boolean isWorkSaved(Long userId, Long workId) {
        return saveWorkPort.isWorkSavedByUser(userId, workId);
    }
}
