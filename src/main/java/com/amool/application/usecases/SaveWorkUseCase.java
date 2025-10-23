package com.amool.application.usecases;

import com.amool.application.port.out.SaveWorkPort;
import com.amool.domain.model.Work;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SaveWorkUseCase {

    private final SaveWorkPort saveWorkPort;

    public SaveWorkUseCase(SaveWorkPort saveWorkPort) {
        this.saveWorkPort = saveWorkPort;
    }

    @Transactional
    public void toggleSaveWork(Long userId, Long workId) {
        if (saveWorkPort.isWorkSavedByUser(userId, workId)) {
            saveWorkPort.removeSavedWorkForUser(userId, workId);
        } else {
            saveWorkPort.saveWorkForUser(userId, workId);
        }
    }

    public boolean isWorkSavedByUser(Long userId, Long workId) {
        return saveWorkPort.isWorkSavedByUser(userId, workId);
    }
    
    public List<Work> getSavedWorksByUser(Long userId) {
        return saveWorkPort.getSavedWorksByUser(userId);
    }
}
