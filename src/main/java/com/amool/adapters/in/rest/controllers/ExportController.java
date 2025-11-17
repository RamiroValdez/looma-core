package com.amool.adapters.in.rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amool.application.usecases.ExportEpubUseCase;
import com.amool.adapters.in.rest.dtos.ExportEpubResponseDto;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private ExportEpubUseCase exportEpubUseCase;

    public ExportController(ExportEpubUseCase exportEpubUseCase) {
        this.exportEpubUseCase = exportEpubUseCase;
    }

    @GetMapping("/epub/{workId}")
    public ResponseEntity<ExportEpubResponseDto> exportEpub(@PathVariable Long workId) {

        String url = exportEpubUseCase.execute(workId);
        return ResponseEntity.ok().body(new ExportEpubResponseDto(url));
    }
}
