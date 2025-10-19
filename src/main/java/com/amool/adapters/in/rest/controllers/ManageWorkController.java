package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.CreateEmptyChapterRequest;
import com.amool.adapters.in.rest.dtos.CreateEmptyChapterResponse;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.CreateEmptyChapterUseCase;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
import com.amool.domain.model.Chapter;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-work")
public class ManageWorkController {

    private final ObtainWorkByIdUseCase obtainWorkByIdUseCase;
    private final CreateEmptyChapterUseCase createEmptyChapterUseCase;

    public ManageWorkController(ObtainWorkByIdUseCase obtainWorkByIdUseCase,
                                CreateEmptyChapterUseCase createEmptyChapterUseCase) {
        this.obtainWorkByIdUseCase = obtainWorkByIdUseCase;
        this.createEmptyChapterUseCase = createEmptyChapterUseCase;
    }

    @GetMapping("/{workId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId) {
        return obtainWorkByIdUseCase.execute(workId)
                .map(WorkMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create-chapter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreateEmptyChapterResponse> createEmptyChapter(@RequestBody CreateEmptyChapterRequest request) {
        try {
            Chapter chapter = createEmptyChapterUseCase.execute(
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
}