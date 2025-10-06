package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.ImagePromptDto;
import com.amool.hexagonal.adapters.in.rest.dtos.ImageUrlResponseDto;
import com.amool.hexagonal.application.port.in.ImageGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    public ImageGenerationController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ImageUrlResponseDto> generate(@RequestBody ImagePromptDto request) {
        String url = imageGenerationService.generateImageUrl(request.prompt());
        return ResponseEntity.ok(new ImageUrlResponseDto(url));
    }
}
