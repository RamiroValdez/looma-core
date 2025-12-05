package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.GetAllLanguages;
import com.amool.domain.model.Language;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final GetAllLanguages getAllLanguages;

    public LanguageController(GetAllLanguages getAllLanguages) {
        this.getAllLanguages = getAllLanguages;
    }

    @GetMapping("/obtain-all")
    public ResponseEntity<List<Language>> getAllLanguages() {
        return ResponseEntity.ok(getAllLanguages.execute());
    }
}
