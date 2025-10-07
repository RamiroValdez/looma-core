package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.CreateEmptyChapterRequest;
import com.amool.hexagonal.adapters.in.rest.dtos.CreateEmptyChapterResponse;
import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.WorkMapper;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.Chapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-work")
public class ManageWorkController {

    private final WorkService workService;
    private final ChapterService chapterService;

    public ManageWorkController(WorkService workService, ChapterService chapterService) {
        this.workService = workService;
        this.chapterService = chapterService;
    }

    @GetMapping("/{workId}")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId) {
        return workService.obtainWorkById(workId)
                .map(WorkMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create-chapter")
    public ResponseEntity<CreateEmptyChapterResponse> createEmptyChapter(@RequestBody CreateEmptyChapterRequest request) {
        try {
            Chapter chapter = chapterService.createEmptyChapter(
                request.getWorkId(),
                request.getLanguageId()
            );

            CreateEmptyChapterResponse response = new CreateEmptyChapterResponse();
            response.setChapterId(chapter.getId());
            response.setContentType(request.getContentType());

            String editorUrl = buildEditorUrl(chapter.getId(), request.getContentType());
            response.setEditorUrl(editorUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String buildEditorUrl(Long chapterId, String contentType) {
        if ("TEXT".equals(contentType)) {
            return "/editor/text/" + chapterId;
        } else if ("IMAGES".equals(contentType)) {
            return "/editor/images/" + chapterId;
        } else {
            return "/editor/" + chapterId; 
        }
    }
}