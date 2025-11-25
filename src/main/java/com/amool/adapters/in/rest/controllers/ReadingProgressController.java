package com.amool.adapters.in.rest.controllers;

import org.springframework.web.bind.annotation.*;

import com.amool.adapters.in.rest.dtos.ReadingProgressDto;
import com.amool.application.usecases.UpdateReadingProgress;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/reading-progress")
public class ReadingProgressController {

    private final UpdateReadingProgress updateReadingProgress;
    
    public ReadingProgressController(UpdateReadingProgress updateReadingProgress) {
        this.updateReadingProgress = updateReadingProgress;
    }
    
    @PostMapping("/update")
    public ResponseEntity<Void> updateReadingProgress(@RequestBody ReadingProgressDto readingProgressDto) {
        boolean result = updateReadingProgress.execute(readingProgressDto.getUserId(), readingProgressDto.getWorkId(), readingProgressDto.getChapterId());
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    
}
