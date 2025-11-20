package com.amool.adapters.in.rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amool.application.usecases.ExportEpubUseCase;
import com.amool.adapters.in.rest.dtos.ExportEpubResponseDto;
import com.amool.application.usecases.ExportPdfUseCase;
import com.amool.adapters.in.rest.dtos.ExportPdfResponseDto;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private ExportEpubUseCase exportEpubUseCase;
    private ExportPdfUseCase exportPdfUseCase;

    public ExportController(ExportEpubUseCase exportEpubUseCase, ExportPdfUseCase exportPdfUseCase) {
        this.exportEpubUseCase = exportEpubUseCase;
        this.exportPdfUseCase = exportPdfUseCase;
    }

    @GetMapping("/epub/{workId}")
    public ResponseEntity<ExportEpubResponseDto> exportEpub(@PathVariable Long workId) {

        String url = exportEpubUseCase.execute(workId);
        return ResponseEntity.ok().body(new ExportEpubResponseDto(url));
    }

    @GetMapping("/pdf/{workId}")
    public ResponseEntity<ExportPdfResponseDto> exportPdf(@PathVariable Long workId) {

        String url = exportPdfUseCase.execute(workId);
        return ResponseEntity.ok().body(new ExportPdfResponseDto(url));
    }

}
