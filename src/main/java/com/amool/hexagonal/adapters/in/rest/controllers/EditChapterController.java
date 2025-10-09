package com.amool.hexagonal.adapters.in.rest.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.application.port.in.ChapterService;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/edit-chapter")
public class EditChapterController {

    private final ChapterService chapterService;

    public EditChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponseDto> getChapterForEdit(@PathVariable Long chapterId) {
        return chapterService.getChapterForEdit(chapterId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
}
