package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.TranslationDto;
import com.amool.application.usecases.CreateLanguageVersionUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translation")
public class TranslationController {

    private final CreateLanguageVersionUseCase createLanguageVersionUseCase;

    public TranslationController(CreateLanguageVersionUseCase createLanguageVersionUseCase) {
        this.createLanguageVersionUseCase = createLanguageVersionUseCase;
    }

    @PostMapping("/create-version")
    public String createTranslationVersion(@RequestBody TranslationDto translationDTO) {
        return createLanguageVersionUseCase.execute(
                                        translationDTO.sourceLanguage(),
                                        translationDTO.targetLanguage(),
                                        translationDTO.originalText());
    }

}
