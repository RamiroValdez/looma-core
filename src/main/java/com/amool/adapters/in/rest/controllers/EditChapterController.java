package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.ExtractTextFromFileUseCase;
import com.amool.application.usecases.GetChapterForEditUseCase;
import com.amool.application.usecases.UpdateChapterUseCase;
import com.amool.application.usecases.ValidateChapterAccessUseCase;
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
import com.amool.adapters.in.rest.dtos.UpdatePriceRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/edit-chapter")
public class EditChapterController {

    private final GetChapterForEditUseCase getChapterForEditUseCase;
    private final UpdateChapterUseCase updateChapterUseCase;
    private final ExtractTextFromFileUseCase extractTextFromFileUseCase;
    private final ValidateChapterAccessUseCase validateChapterAccessUseCase;

    public EditChapterController(
            GetChapterForEditUseCase getChapterForEditUseCase,
            UpdateChapterUseCase updateChapterUseCase,
            ExtractTextFromFileUseCase extractTextFromFileUseCase,
            ValidateChapterAccessUseCase validateChapterAccessUseCase) {
        this.getChapterForEditUseCase = getChapterForEditUseCase;
        this.updateChapterUseCase = updateChapterUseCase;
        this.extractTextFromFileUseCase = extractTextFromFileUseCase;
        this.validateChapterAccessUseCase = validateChapterAccessUseCase;
    }

    @GetMapping("/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponseDto> getChapterForEdit(
            @PathVariable Long chapterId,
            @RequestParam(value = "language", required = false) String language) {

        Long userId = extractUserIdFromAuthentication();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                validateChapterAccessUseCase.validateAccess(chapterId, userId);

        if (!accessResult.isChapterFound()) {
            return ResponseEntity.notFound().build();
        }

        if (!accessResult.isAccessGranted()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return getChapterForEditUseCase.execute(chapterId, language)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateChapter(
            @PathVariable Long chapterId,
            @RequestBody UpdateChapterRequest updateRequest) {

        Long userId = extractUserIdFromAuthentication();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                validateChapterAccessUseCase.validateAccess(chapterId, userId);

        if (!accessResult.isChapterFound()) {
            return ResponseEntity.notFound().build();
        }

        if (!accessResult.isAccessGranted()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean updated = updateChapterUseCase.execute(chapterId, updateRequest);
        return updated ? ResponseEntity.ok("Capítulo actualizado") : ResponseEntity.notFound().build();
    }

    @PatchMapping("/price/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateChapterPrice(
            @PathVariable Long chapterId,
            @RequestBody UpdatePriceRequest request) {

        Long userId = extractUserIdFromAuthentication();
        if (userId == null) {
            throw new SecurityException("Usuario no autenticado");
        }

        ValidateChapterAccessUseCase.ChapterAccessResult accessResult =
                validateChapterAccessUseCase.validateAccess(chapterId, userId);

        if (!accessResult.isChapterFound()) {
            throw new java.util.NoSuchElementException("Capítulo no encontrado");
        }

        if (!accessResult.isAccessGranted()) {
            throw new SecurityException("Acceso denegado");
        }

        if (request == null || request.price() == null || request.price().signum() < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        UpdateChapterRequest update = new UpdateChapterRequest();
        update.setPrice(request.price());

        boolean updated = updateChapterUseCase.execute(chapterId, update);
        if (!updated) {
            throw new java.util.NoSuchElementException("Capítulo no encontrado");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> importText(@RequestPart("file") MultipartFile file) {
        if (!isValidFile(file)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String text = extractTextFromFileUseCase.execute(file.getBytes(), file.getOriginalFilename());
            return ResponseEntity.ok(new FileTextResponseDto(text));
        } catch (IllegalArgumentException e) {
            return handleFileExtractionError(e);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al leer el archivo."));
        }
    }

    private Long extractUserIdFromAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return null;
        }
        return principal.getUserId();
    }

    private boolean isValidFile(MultipartFile file) {
        return file != null && !file.isEmpty() && StringUtils.hasText(file.getOriginalFilename());
    }

    private ResponseEntity<?> handleFileExtractionError(IllegalArgumentException e) {
        HttpStatus status = e.getMessage() != null && e.getMessage().contains("Formato de archivo")
                ? HttpStatus.UNSUPPORTED_MEDIA_TYPE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
    }
}

