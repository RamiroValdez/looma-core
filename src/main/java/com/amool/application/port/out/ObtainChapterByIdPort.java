package com.amool.application.port.out;

import java.util.Optional;

import com.amool.domain.model.Chapter;

public interface ObtainChapterByIdPort {

    public Optional<Chapter> obtainChapterById(Long chapterId);
    
}
