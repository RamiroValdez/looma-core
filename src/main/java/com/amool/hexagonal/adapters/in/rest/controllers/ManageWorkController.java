package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.CreateEmptyChapterRequest;
import com.amool.hexagonal.adapters.in.rest.dtos.CreateEmptyChapterResponse;
import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.WorkMapper;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.Chapter;
import com.amool.hexagonal.domain.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId) {
        return workService.obtainWorkById(workId)
                .map(WorkMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create-chapter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreateEmptyChapterResponse> createEmptyChapter(@RequestBody CreateEmptyChapterRequest request) {
        try {
            Chapter chapter = chapterService.createEmptyChapter(
                request.getWorkId(),
                request.getLanguageId(),
                request.getContentType()
            );

            CreateEmptyChapterResponse response = new CreateEmptyChapterResponse();
            response.setChapterId(chapter.getId());
            response.setContentType(request.getContentType());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{workId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteWork(@PathVariable Long workId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }
            
            User user = (User) authentication.getPrincipal();
            workService.deleteWork(workId, user.getId());
            
            return ResponseEntity.noContent().build();
            
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}