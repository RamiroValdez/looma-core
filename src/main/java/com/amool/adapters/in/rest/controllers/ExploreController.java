package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.dtos.WorkSearchFilterDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.SearchAndFiltrateUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/explore")
public class ExploreController {

    private final SearchAndFiltrateUseCase searchAndFiltrateUseCase;

    public ExploreController(SearchAndFiltrateUseCase searchAndFiltrateUseCase) {
        this.searchAndFiltrateUseCase = searchAndFiltrateUseCase;
    }

    @PostMapping
    public ResponseEntity<Page<WorkResponseDto>> explore(
            @RequestBody WorkSearchFilterDto filterDto,
            Pageable pageable
    ) {
        Page<WorkResponseDto> dtoPage = searchAndFiltrateUseCase
                .execute(WorkMapper.toDomain(filterDto), pageable)
                .map(WorkMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

}
