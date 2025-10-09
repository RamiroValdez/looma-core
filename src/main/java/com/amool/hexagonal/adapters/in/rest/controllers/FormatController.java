package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.FormatDto;
import com.amool.hexagonal.adapters.in.rest.mappers.FormatMapper;
import com.amool.hexagonal.application.port.in.FormatService;
import com.amool.hexagonal.domain.model.Format;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/format")
public class FormatController {

    private final FormatService formatService;

    public FormatController(FormatService formatService) {
        this.formatService = formatService;
    }

    @GetMapping("/obtain-all")
    public ResponseEntity<List<FormatDto>> obtainAllFormats() {
        List<Format> formats = formatService.getAllFormats();
        if (formats == null || formats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<FormatDto> formatDtos = FormatMapper.toDtoList(formats);
        return ResponseEntity.ok(formatDtos);
    }

}
