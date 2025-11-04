package com.amool.adapters.in.rest.controllers;

import org.springframework.web.bind.annotation.*;

import com.amool.adapters.in.rest.dtos.ReadingProgressDto;
import com.amool.application.usecases.UpdateReadingProgressUseCase;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/reading-progress")
public class ReadingProgressController {

    private final UpdateReadingProgressUseCase updateReadingProgressUseCase;
    
    public ReadingProgressController(UpdateReadingProgressUseCase updateReadingProgressUseCase) {
        this.updateReadingProgressUseCase = updateReadingProgressUseCase;
    }
    
    @PostMapping("/update")
    public ResponseEntity<Void> updateReadingProgress(@RequestBody ReadingProgressDto readingProgressDto) {
        boolean result = updateReadingProgressUseCase.execute(readingProgressDto.getUserId(), readingProgressDto.getWorkId(), readingProgressDto.getChapterId());
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    
}
