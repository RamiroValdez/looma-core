package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.FormatDto;
import com.amool.adapters.in.rest.mappers.FormatMapper;
import com.amool.application.usecases.ObtainAllFormatsUseCase;
import com.amool.domain.model.Format;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/format")
public class FormatController {

    private final ObtainAllFormatsUseCase obtainAllFormatsUseCase;

    public FormatController(ObtainAllFormatsUseCase obtainAllFormatsUseCase) {
        this.obtainAllFormatsUseCase = obtainAllFormatsUseCase;
    }

    @GetMapping("/obtain-all")
    public ResponseEntity<List<FormatDto>> obtainAllFormats() {
        List<Format> formats = obtainAllFormatsUseCase.execute();
        if (formats == null || formats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<FormatDto> formatDtos = FormatMapper.toDtoList(formats);
        return ResponseEntity.ok(formatDtos);
    }

}
