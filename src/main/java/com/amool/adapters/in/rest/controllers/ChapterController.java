package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.ChapterWithContentDto;
import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.adapters.in.rest.dtos.UpdateChapterContentRequest;
import com.amool.adapters.in.rest.mappers.ChapterMapper;
import com.amool.application.port.out.LoadChapterContentPort;
import com.amool.application.port.out.SaveChapterContentPort;
import com.amool.application.port.out.LoadWorkOwnershipPort;
import com.amool.application.usecases.*;
import com.amool.domain.model.ChapterContent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import com.amool.security.JwtUserPrincipal;
import com.amool.adapters.in.rest.dtos.SchedulePublicationRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amool.application.usecases.LikeChapterUseCase;
import com.amool.application.usecases.UnlikeChapterUseCase;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChapterController {

    private final GetChapterWithContentUseCase getChapterWithContentUseCase;
    private final DeleteChapterUseCase deleteChapterUseCase;
    private final PublishChapterUseCase publishChapterUseCase;
    private final SchedulePublicationUseCase schedulePublicationUseCase;
    private final CancelScheduledPublicationUseCase cancelScheduledPublicationUseCase;
    private final LoadChapterContentPort loadChapterContentPort;
    private final SaveChapterContentPort saveChapterContentPort;
    private final LoadWorkOwnershipPort loadWorkOwnershipPort;
    private final LikeChapterUseCase likeChapterUseCase;
    private final UnlikeChapterUseCase unlikeChapterUseCase;
    private static final java.time.ZoneId AR = java.time.ZoneId.of("America/Argentina/Buenos_Aires");
    private static final Logger log = LoggerFactory.getLogger(ChapterController.class);

    @GetMapping("/work/{workId}/chapter/{chapterId}")
    public ResponseEntity<ChapterWithContentDto> getChapter(
            @PathVariable String workId,
            @PathVariable String chapterId,
            @RequestParam(required = false, defaultValue = "es") String language) {
        
        return getChapterWithContentUseCase.execute(Long.valueOf(workId), Long.valueOf(chapterId), language)
                .map(chapterWithContent -> {
                    Optional<ChapterContent> chapterContent = loadChapterContentPort.loadContent(workId, chapterId, language);
                    
                    List<String> availableLanguages = loadChapterContentPort.getAvailableLanguages(workId, chapterId);
                    
                    String content = chapterContent
                            .map(ChapterContent::getContentByLanguage)
                            .map(contentMap -> contentMap.getOrDefault(language, 
                                contentMap.values().stream().findFirst().orElse("")))
                            .orElse("");
                    return ChapterMapper.toDto(chapterWithContent, content, availableLanguages);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.<ChapterWithContentDto>notFound().build());
    }
    @PostMapping("/work/{workId}/chapter/{chapterId}/content")
    public ResponseEntity<ChapterContent> updateChapterContent(
            @PathVariable String workId,
            @PathVariable String chapterId,
            @Valid @RequestBody UpdateChapterContentRequest request) {

        if (!workId.equals(request.workId()) || !chapterId.equals(request.chapterId())) {
            return ResponseEntity.badRequest().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }
        Long workIdLong = Long.valueOf(workId);
        boolean isOwner = loadWorkOwnershipPort.isOwner(workIdLong, principal.getUserId());
        if (!isOwner) {
            return ResponseEntity.status(403).build();
        }
        ChapterContent updated = saveChapterContentPort.saveContent(
            request.workId(),
            request.chapterId(),
            request.language(),
            request.content()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/work/{workId}/chapter/{chapterId}/delete")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable String workId,
            @PathVariable String chapterId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }

        Long workIdLong = Long.valueOf(workId);
        boolean isOwner = loadWorkOwnershipPort.isOwner(workIdLong, principal.getUserId());
        if (!isOwner) {
            return ResponseEntity.status(403).build();
        }
        try {
            deleteChapterUseCase.execute(workIdLong, Long.valueOf(chapterId));
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/work/{workId}/chapter/{chapterId}/publish")
    public ResponseEntity<Void> publishChapter(
            @PathVariable String workId,
            @PathVariable String chapterId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }

        try {
            publishChapterUseCase.execute(Long.valueOf(workId), Long.valueOf(chapterId), principal.getUserId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/work/{workId}/chapter/{chapterId}/schedule")
    public ResponseEntity<Void> scheduleChapter(
            @PathVariable String workId,
            @PathVariable String chapterId,
            @RequestBody SchedulePublicationRequestDto request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }

        try {
            log.debug("Schedule request.when raw: {}", request.when());
            java.time.Instant when;
            try {
                when = java.time.OffsetDateTime.parse(request.when()).toInstant();
                log.debug("Parsed as OffsetDateTime -> Instant(UTC): {}", when);
            } catch (java.time.format.DateTimeParseException ex) {
                java.time.LocalDateTime local = java.time.LocalDateTime.parse(request.when());
                when = local.atZone(AR).toInstant();
                log.debug("Parsed as LocalDateTime AR -> Instant(UTC): {} (local: {})", when, local);
            }
            java.time.LocalDateTime persistedLocal = java.time.LocalDateTime.ofInstant(when, AR);
            log.debug("Will persist local AR time: {} (computed from Instant)", persistedLocal);
            schedulePublicationUseCase.execute(Long.valueOf(workId), Long.valueOf(chapterId), when, principal.getUserId());
            return ResponseEntity.noContent().build();
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/work/{workId}/chapter/{chapterId}/schedule")
    public ResponseEntity<Void> cancelSchedule(
            @PathVariable String workId,
            @PathVariable String chapterId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }

        try {
            cancelScheduledPublicationUseCase.execute(Long.valueOf(workId), Long.valueOf(chapterId), principal.getUserId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/work/{workId}/chapter/{chapterId}/like")
    public ResponseEntity<LikeResponseDto> likeChapter(
            @PathVariable Long workId,
            @PathVariable Long chapterId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
    
        Long userId = userPrincipal.getUserId();
        LikeResponseDto response = likeChapterUseCase.execute(chapterId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/work/{workId}/chapter/{chapterId}/like")
    public ResponseEntity<LikeResponseDto> unlikeChapter(
            @PathVariable Long workId,
            @PathVariable Long chapterId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
    
        Long userId = userPrincipal.getUserId();
        LikeResponseDto response = unlikeChapterUseCase.execute(chapterId, userId);
        return ResponseEntity.ok(response);
    }

}
