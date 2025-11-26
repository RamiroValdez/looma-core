package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.TranslationDto;
import com.amool.application.usecases.CreateLanguageVersion;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translation")
public class TranslationController {

    private final CreateLanguageVersion createLanguageVersion;

    public TranslationController(CreateLanguageVersion createLanguageVersion) {
        this.createLanguageVersion = createLanguageVersion;
    }

    @PostMapping("/create-version")
    public String createTranslationVersion(@RequestBody TranslationDto translationDTO) {
        return createLanguageVersion.execute(
                                        translationDTO.sourceLanguage(),
                                        translationDTO.targetLanguage(),
                                        translationDTO.originalText());
    }

}
