package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.CreateEmptyChapterRequest;
import com.amool.adapters.in.rest.dtos.CreateEmptyChapterResponse;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.dtos.ChapterDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.CreateEmptyChapterUseCase;
import com.amool.application.usecases.ObtainWorkByIdUseCase;
import com.amool.domain.model.Chapter;
import com.amool.hexagonal.application.port.out.SubscriptionQueryPort;
import com.amool.security.JwtUserPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/manage-work")
public class ManageWorkController {

    private final ObtainWorkByIdUseCase obtainWorkByIdUseCase;
    private final CreateEmptyChapterUseCase createEmptyChapterUseCase;
    private final SubscriptionQueryPort subscriptionQueryPort;

    public ManageWorkController(ObtainWorkByIdUseCase obtainWorkByIdUseCase,
                                CreateEmptyChapterUseCase createEmptyChapterUseCase,
                                SubscriptionQueryPort subscriptionQueryPort) {
        this.obtainWorkByIdUseCase = obtainWorkByIdUseCase;
        this.createEmptyChapterUseCase = createEmptyChapterUseCase;
        this.subscriptionQueryPort = subscriptionQueryPort;
    }

    @GetMapping("/{workId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId) {
        return obtainWorkByIdUseCase.execute(workId)
                .map(WorkMapper::toDto)
                .map(dto -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
                        Long userId = principal.getUserId();
                        Long authorId = dto.getCreator() != null ? dto.getCreator().getId() : null;
                        if (authorId != null && authorId.equals(userId)) {
                            dto.setSubscribedToAuthor(true);
                            dto.setSubscribedToWork(true);
                            List<Long> allChapterIds = new ArrayList<>();
                            if (dto.getChapters() != null) {
                                for (ChapterDto ch : dto.getChapters()) {
                                    if (ch != null && ch.getId() != null) allChapterIds.add(ch.getId());
                                }
                            }
                            dto.setUnlockedChapters(allChapterIds);
                        } else {
                            boolean subAuthor = authorId != null && subscriptionQueryPort.isSubscribedToAuthor(userId, authorId);
                            boolean subWork = subscriptionQueryPort.isSubscribedToWork(userId, workId);
                            var unlocked = subscriptionQueryPort.unlockedChapters(userId, workId);
                            dto.setSubscribedToAuthor(subAuthor);
                            dto.setSubscribedToWork(subWork);
                            dto.setUnlockedChapters(unlocked);
                        }
                    }
                    return ResponseEntity.ok(dto);
                })
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