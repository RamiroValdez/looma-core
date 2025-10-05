package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.TranslationDto;
import com.amool.hexagonal.application.port.in.TranslationSystemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translation")
public class TranslationController {

    private TranslationSystemService translationSystemService;

    public TranslationController(TranslationSystemService translationSystemService) {
        this.translationSystemService = translationSystemService;
    }

    @PostMapping("/create-version")
    public String createTranslationVersion(@RequestBody TranslationDto translationDTO) {
        return translationSystemService.CreateLanguageVersion(
                                        translationDTO.sourceLanguage(),
                                        translationDTO.targetLanguage(),
                                        translationDTO.originalText());
    }

}
