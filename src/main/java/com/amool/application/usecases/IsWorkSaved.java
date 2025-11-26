package com.amool.application.usecases;

import com.amool.application.port.out.SaveWorkPort;

public class IsWorkSaved {

    private final SaveWorkPort saveWorkPort;

    public IsWorkSaved(SaveWorkPort saveWorkPort) {
        this.saveWorkPort = saveWorkPort;
    }

    public boolean execute(Long userId, Long workId) {
        return saveWorkPort.isWorkSavedByUser(userId, workId);
    }
}
