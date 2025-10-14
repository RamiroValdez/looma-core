package com.amool.hexagonal.adapters.in.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterResponseDto;
import com.amool.hexagonal.adapters.in.rest.dtos.UpdateChapterRequest;
import com.amool.hexagonal.adapters.in.rest.dtos.FileTextResponseDto;
import com.amool.hexagonal.application.port.in.ChapterService;
import com.amool.hexagonal.application.port.in.FileToTextService;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/edit-chapter")
public class EditChapterController {

    private final ChapterService chapterService;
    private final FileToTextService fileToTextService;

    public EditChapterController(ChapterService chapterService, FileToTextService fileToTextService) {
        this.chapterService = chapterService;
        this.fileToTextService = fileToTextService;
    }

    @GetMapping("/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChapterResponseDto> getChapterForEdit(@PathVariable Long chapterId,
                                                                @RequestParam(value = "language", required = false) String language) {
        return chapterService.getChapterForEdit(chapterId, language)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/update/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateChapter (
        @PathVariable Long chapterId,
        @RequestBody UpdateChapterRequest updateRequest) {
        
        boolean updated = chapterService.updateChapter(chapterId, updateRequest);
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
            String text = fileToTextService.extractText(file.getBytes(), filename);
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
