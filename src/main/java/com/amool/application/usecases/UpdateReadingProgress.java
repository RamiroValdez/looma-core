package com.amool.application.usecases;

import com.amool.application.port.out.ReadingProgressPort;

public class UpdateReadingProgress {
    
    private final ReadingProgressPort readingProgressPort;

    public UpdateReadingProgress(ReadingProgressPort readingProgressPort) {
        this.readingProgressPort = readingProgressPort;
    }

    public boolean execute(Long userId, Long workId, Long chapterId) {
        boolean result = readingProgressPort.create(userId, workId, chapterId);

        if(result) {
            readingProgressPort.addToHistory(userId, workId, chapterId);
            return true;
        } else {
            return false;
        }
    }
}
