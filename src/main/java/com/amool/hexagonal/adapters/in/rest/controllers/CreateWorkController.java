package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.CreateWorkDto;
import com.amool.hexagonal.adapters.in.rest.dtos.TagSuggestionRequestDto;
import com.amool.hexagonal.adapters.in.rest.dtos.TagSuggestionResponseDto;
import com.amool.hexagonal.application.port.in.TagSuggestionService;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/create-work")
public class CreateWorkController {

    private final WorkService workService;
    private final TagSuggestionService tagSuggestionService;


    public CreateWorkController(WorkService workService, TagSuggestionService tagSuggestionService) {
        this.workService = workService;
        this.tagSuggestionService = tagSuggestionService;
    }


    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<Long> saveWork(
            @RequestPart("work") CreateWorkDto createWorkDto,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile,
            @RequestPart("banner") MultipartFile bannerFile,
            @AuthenticationPrincipal JwtUserPrincipal principal
            ) {
                try {
                    Long result = this.workService.createWork(
                            createWorkDto.title(),
                            createWorkDto.description(),
                            createWorkDto.categoryIds(),
                            createWorkDto.formatId(),
                            createWorkDto.originalLanguageId(),
                            createWorkDto.tagIds(),
                            createWorkDto.coverIaUrl(),
                            coverFile,
                            bannerFile,
                            principal.getUserId());

                    return  ResponseEntity.ok(result);

                } catch (Exception e) {
                    return ResponseEntity.badRequest().build();
                }
    }

    @PostMapping(value = "/suggest-tags")
    public ResponseEntity<TagSuggestionResponseDto> suggestTags(@RequestBody TagSuggestionRequestDto request) {
        if (request == null || !StringUtils.hasText(request.description())) {
            return ResponseEntity.badRequest().build();
        }

        var suggestions = tagSuggestionService.suggestTags(
                request.description(),
                request.title(),
                request.existingTags() == null ? Set.of() : request.existingTags()
        );

        return ResponseEntity.ok(new TagSuggestionResponseDto(suggestions));
    }
}
