package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dto.ChapterWithContentDto;
import com.amool.hexagonal.adapters.in.rest.dto.UpdateChapterContentRequest;
import com.amool.hexagonal.adapters.in.rest.mappers.ChapterMapper;
import com.amool.hexagonal.application.port.in.GetChapterUseCase;
import com.amool.hexagonal.application.port.out.LoadChapterContentPort;
import com.amool.hexagonal.application.port.out.SaveChapterContentPort;
import com.amool.hexagonal.domain.model.ChapterContent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChapterController {

    private final GetChapterUseCase getChapterUseCase;
    private final LoadChapterContentPort loadChapterContentPort;
    private final SaveChapterContentPort saveChapterContentPort;

    @GetMapping("/books/{bookId}/chapters/{chapterId}")
    public ResponseEntity<ChapterWithContentDto> getChapter(
            @PathVariable String bookId,
            @PathVariable String chapterId,
            @RequestParam(required = false, defaultValue = "es") String language) {
        
        return getChapterUseCase.getChapterWithContent(Long.valueOf(bookId), Long.valueOf(chapterId), language)
                .map(chapterWithContent -> {
                    Optional<ChapterContent> chapterContent = loadChapterContentPort.loadContent(bookId, chapterId, language);
                    
                    List<String> availableLanguages = loadChapterContentPort.getAvailableLanguages(bookId, chapterId);
                    
                    String content = chapterContent
                            .map(cc -> cc.getContentByLanguage())
                            .map(contentMap -> contentMap.getOrDefault(language, 
                                contentMap.values().stream().findFirst().orElse("")))
                            .orElse("");
                    
                    return ChapterMapper.toDto(chapterWithContent, content, availableLanguages);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.<ChapterWithContentDto>notFound().build());
    }
    
    @PostMapping("/books/{bookId}/chapters/{chapterId}/content")
    public ResponseEntity<ChapterContent> updateChapterContent(
            @PathVariable String bookId,
            @PathVariable String chapterId,
            @Valid @RequestBody UpdateChapterContentRequest request) {
        
        if (!bookId.equals(request.workId()) || !chapterId.equals(request.chapterId())) {
            return ResponseEntity.badRequest().build();
        }
        
        ChapterContent updated = saveChapterContentPort.saveContent(
            request.workId(),
            request.chapterId(),
            request.language(),
            request.content()
        );
        return ResponseEntity.ok(updated);
    }

}
