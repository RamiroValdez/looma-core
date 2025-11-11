package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.CreateWorkDto;
import com.amool.adapters.in.rest.dtos.TagSuggestionRequestDto;
import com.amool.adapters.in.rest.dtos.TagSuggestionResponseDto;
import com.amool.application.usecases.CreateWorkUseCase;
import com.amool.application.usecases.SaveNotificationUseCase;
import com.amool.application.usecases.SuggestTagsUseCase;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/create-work")
public class CreateWorkController {

    private final CreateWorkUseCase createWorkUseCase;
    private final SuggestTagsUseCase suggestTagsUseCase;
    private final SaveNotificationUseCase saveNotificationUseCase;


    public CreateWorkController(CreateWorkUseCase createWorkUseCase, SuggestTagsUseCase suggestTagsUseCase, SaveNotificationUseCase saveNotificationUseCase) {
        this.createWorkUseCase = createWorkUseCase;
        this.suggestTagsUseCase = suggestTagsUseCase;
        this.saveNotificationUseCase = saveNotificationUseCase;
    }


    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<Long> saveWork(
            @RequestPart("work") CreateWorkDto createWorkDto,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile,
            @RequestPart("banner") MultipartFile bannerFile,
            @AuthenticationPrincipal JwtUserPrincipal principal
            ) {
                try {
                    Long result = this.createWorkUseCase.execute(
                            createWorkDto.title(),
                            createWorkDto.description(),
                            createWorkDto.categoryIds(),
                            createWorkDto.formatId(),
                            createWorkDto.originalLanguageId(),
                            createWorkDto.price(),
                            createWorkDto.tagIds(),
                            createWorkDto.coverIaUrl(),
                            coverFile,
                            bannerFile,
                            principal.getUserId());
                    
                        this.saveNotificationUseCase.createLectorNotification(result, principal.getUserId());
                    

                    return ResponseEntity.ok(result);

                } catch (Exception e) {
                    return ResponseEntity.badRequest().build();
                }
    }

    @PostMapping(value = "/suggest-tags")
    public ResponseEntity<TagSuggestionResponseDto> suggestTags(@RequestBody TagSuggestionRequestDto request) {
        if (request == null || !StringUtils.hasText(request.description())) {
            return ResponseEntity.badRequest().build();
        }

        var suggestions = suggestTagsUseCase.execute(
                request.description(),
                request.title(),
                request.existingTags() == null ? Set.of() : request.existingTags()
        );

        return ResponseEntity.ok(new TagSuggestionResponseDto(suggestions));
    }
}
