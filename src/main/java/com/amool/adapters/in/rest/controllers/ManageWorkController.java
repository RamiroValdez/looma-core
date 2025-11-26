package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.CreateEmptyChapterRequest;
import com.amool.adapters.in.rest.dtos.CreateEmptyChapterResponse;
import com.amool.adapters.in.rest.dtos.UpdateWorkDto;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.CreateEmptyChapter;
import com.amool.application.usecases.GetWorkPermissions;
import com.amool.application.usecases.ObtainWorkById;
import com.amool.application.usecases.UpdateWork;
import com.amool.domain.model.Chapter;
import com.amool.domain.model.WorkPermissions;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-work")
public class ManageWorkController {

    private final ObtainWorkById obtainWorkById;
    private final CreateEmptyChapter createEmptyChapter;
    private final GetWorkPermissions getWorkPermissions;
    private final UpdateWork updateWork;

    public ManageWorkController(ObtainWorkById obtainWorkById,
                                CreateEmptyChapter createEmptyChapter,
                                GetWorkPermissions getWorkPermissions,
                                UpdateWork updateWork) {
        this.obtainWorkById = obtainWorkById;
        this.createEmptyChapter = createEmptyChapter;
        this.getWorkPermissions = getWorkPermissions;
        this.updateWork = updateWork;
    }

    @GetMapping("/{workId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId, @AuthenticationPrincipal JwtUserPrincipal user) {
        return obtainWorkById.execute(workId, user.getUserId())
                .map(work -> {
                    WorkResponseDto dto = WorkMapper.toDto(work);

                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
                        Long userId = principal.getUserId();
                        WorkPermissions permissions = getWorkPermissions.execute(work, userId);

                        dto.setSubscribedToAuthor(permissions.isSubscribedToAuthor());
                        dto.setSubscribedToWork(permissions.isSubscribedToWork());
                        dto.setUnlockedChapters(permissions.getUnlockedChapters());
                    }

                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/create-chapter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreateEmptyChapterResponse> createEmptyChapter(@RequestBody CreateEmptyChapterRequest request) {
        try {
            Chapter chapter = createEmptyChapter.execute(
                request.getWorkId(),
                request.getLanguageId(),
                request.getContentType()
            );

            CreateEmptyChapterResponse response = new CreateEmptyChapterResponse();
            response.setChapterId(chapter.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping(value = "/{workId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> updateWork( 
            @PathVariable Long workId,
            @RequestBody UpdateWorkDto request,
            @AuthenticationPrincipal JwtUserPrincipal user) {
    
        try {
            Boolean updatedWorkId = updateWork.execute(workId,
                                            request.price(),
                                            request.tagIds(), 
                                            request.categoryIds(),
                                            request.state());
            return ResponseEntity.ok(updatedWorkId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}