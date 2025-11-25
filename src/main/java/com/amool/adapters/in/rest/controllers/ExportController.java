package com.amool.adapters.in.rest.controllers;

import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amool.application.usecases.ExportEpub;
import com.amool.adapters.in.rest.dtos.ExportEpubResponseDto;
import com.amool.application.usecases.ExportPdf;
import com.amool.adapters.in.rest.dtos.ExportPdfResponseDto;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportEpub exportEpub;
    private final ExportPdf exportPdf;

    public ExportController(ExportEpub exportEpub, ExportPdf exportPdf) {
        this.exportEpub = exportEpub;
        this.exportPdf = exportPdf;
    }

    @GetMapping("/epub/{workId}")
    public ResponseEntity<ExportEpubResponseDto> exportEpub(@PathVariable Long workId, @AuthenticationPrincipal JwtUserPrincipal userDetails) {

        String url = exportEpub.execute(workId, userDetails.getUserId());
        return ResponseEntity.ok().body(new ExportEpubResponseDto(url));
    }

    @GetMapping("/pdf/{workId}")
    public ResponseEntity<ExportPdfResponseDto> exportPdf(@PathVariable Long workId, @AuthenticationPrincipal JwtUserPrincipal userDetails) {

        String url = exportPdf.execute(workId, userDetails.getUserId());
        return ResponseEntity.ok().body(new ExportPdfResponseDto(url));
    }

}
