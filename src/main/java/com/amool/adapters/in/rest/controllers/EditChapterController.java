package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.ExtractTextFromFileUseCase;
import com.amool.application.usecases.GetChapterForEditUseCase;
import com.amool.application.usecases.UpdateChapterUseCase;
import com.amool.application.port.out.LoadChapterPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.application.port.out.SubscriptionQueryPort;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.amool.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.adapters.in.rest.dtos.UpdateChapterRequest;
import com.amool.adapters.in.rest.dtos.FileTextResponseDto;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/edit-chapter")
public class EditChapterController {

    private final GetChapterForEditUseCase getChapterForEditUseCase;
    private final UpdateChapterUseCase updateChapterUseCase;
    private final ExtractTextFromFileUseCase extractTextFromFileUseCase;
    private final LoadChapterPort loadChapterPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final SubscriptionQueryPort subscriptionQueryPort;

    public EditChapterController(
            GetChapterForEditUseCase getChapterForEditUseCase,
            UpdateChapterUseCase updateChapterUseCase,
            ExtractTextFromFileUseCase extractTextFromFileUseCase,
            LoadChapterPort loadChapterPort,
            ObtainWorkByIdPort obtainWorkByIdPort,
            SubscriptionQueryPort subscriptionQueryPort) {
        this.getChapterForEditUseCase = getChapterForEditUseCase;
        this.updateChapterUseCase = updateChapterUseCase;
        this.extractTextFromFileUseCase = extractTextFromFileUseCase;
        this.loadChapterPort = loadChapterPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.subscriptionQueryPort = subscriptionQueryPort;
    }

    @GetMapping("/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponseDto> getChapterForEdit(@PathVariable Long chapterId,
                                                                @RequestParam(value = "language", required = false) String language) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = principal.getUserId();

        Optional<com.amool.domain.model.Chapter> chapterOpt = loadChapterPort.loadChapterForEdit(chapterId);
        if (chapterOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long workId = chapterOpt.get().getWorkId();
        Long authorId = obtainWorkByIdPort.obtainWorkById(workId)
                .map(w -> w.getCreator() != null ? w.getCreator().getId() : null)
                .orElse(null);

        boolean isOwner = (authorId != null && authorId.equals(userId));
        boolean hasSub = isOwner
                || (authorId != null && subscriptionQueryPort.isSubscribedToAuthor(userId, authorId))
                || subscriptionQueryPort.isSubscribedToWork(userId, workId)
                || subscriptionQueryPort.unlockedChapters(userId, workId).contains(chapterId);

        if (!hasSub) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return getChapterForEditUseCase.execute(chapterId, language)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/update/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateChapter (
        @PathVariable Long chapterId,
        @RequestBody UpdateChapterRequest updateRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long userId = principal.getUserId();

        Optional<com.amool.domain.model.Chapter> chapterOpt = loadChapterPort.loadChapterForEdit(chapterId);
        if (chapterOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long workId = chapterOpt.get().getWorkId();
        Long authorId = obtainWorkByIdPort.obtainWorkById(workId)
                .map(w -> w.getCreator() != null ? w.getCreator().getId() : null)
                .orElse(null);

        boolean isOwner = (authorId != null && authorId.equals(userId));
        boolean hasSub = isOwner
                || (authorId != null && subscriptionQueryPort.isSubscribedToAuthor(userId, authorId))
                || subscriptionQueryPort.isSubscribedToWork(userId, workId)
                || subscriptionQueryPort.unlockedChapters(userId, workId).contains(chapterId);

        if (!hasSub) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean updated = updateChapterUseCase.execute(chapterId, updateRequest);
        return updated ? ResponseEntity.ok("Capítulo actualizado") : ResponseEntity.notFound().build();
    }


    @PostMapping(value = "/import-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> importText(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String text = extractTextFromFileUseCase.execute(file.getBytes(), filename);
            return ResponseEntity.ok(new FileTextResponseDto(text));
        } catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage() != null && e.getMessage().contains("Formato de archivo")
                    ? HttpStatus.UNSUPPORTED_MEDIA_TYPE
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al leer el archivo."));
        }
    }

}
