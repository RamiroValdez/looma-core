package com.amool.adapters.in.rest.controllers;

import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final ExportEpubUseCase exportEpubUseCase;
    private final ExportPdfUseCase exportPdfUseCase;

    public ExportController(ExportEpubUseCase exportEpubUseCase, ExportPdfUseCase exportPdfUseCase) {
        this.exportEpubUseCase = exportEpubUseCase;
        this.exportPdfUseCase = exportPdfUseCase;
    }

    @GetMapping("/epub/{workId}")
    public ResponseEntity<ExportEpubResponseDto> exportEpub(@PathVariable Long workId, @AuthenticationPrincipal JwtUserPrincipal userDetails) {

        String url = exportEpubUseCase.execute(workId, userDetails.getUserId());
        return ResponseEntity.ok().body(new ExportEpubResponseDto(url));
    }

    @GetMapping("/pdf/{workId}")
    public ResponseEntity<ExportPdfResponseDto> exportPdf(@PathVariable Long workId, @AuthenticationPrincipal JwtUserPrincipal userDetails) {

        String url = exportPdfUseCase.execute(workId, userDetails.getUserId());
        return ResponseEntity.ok().body(new ExportPdfResponseDto(url));
    }

}
