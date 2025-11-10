package com.amool.application.usecases;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.domain.model.Work;

import java.util.List;

public class GetSavedWorksUseCase {

    private final SaveWorkPort saveWorkPort;

    public GetSavedWorksUseCase(SaveWorkPort saveWorkPort) {
        this.saveWorkPort = saveWorkPort;
    }

    public List<Work> execute(Long userId) {
        return saveWorkPort.getSavedWorksByUser(userId);
    }
}
