package com.amool.application.usecases;

import com.amool.application.port.out.ReadingProgressPort;

public class UpdateReadingProgressUseCase {
    
    private final ReadingProgressPort readingProgressPort;

    public UpdateReadingProgressUseCase(ReadingProgressPort readingProgressPort) {
        this.readingProgressPort = readingProgressPort;
    }

    public boolean execute(Long userId, Long workId, Long chapterId) {
        boolean result = readingProgressPort.create(userId, workId, chapterId);
        
        return result;
    }
}
