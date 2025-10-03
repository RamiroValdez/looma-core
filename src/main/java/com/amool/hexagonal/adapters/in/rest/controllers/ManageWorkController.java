package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.WorkMapper;
import com.amool.hexagonal.application.port.in.ObtainWorkByIdUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manage-work")
public class ManageWorkController {

    private ObtainWorkByIdUseCase obtainWorkByIdUseCase;

    public ManageWorkController(ObtainWorkByIdUseCase obtainWorkByIdUseCase) {
        this.obtainWorkByIdUseCase = obtainWorkByIdUseCase;
    }

    @GetMapping("/{workId}")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId) {
        return obtainWorkByIdUseCase.execute(workId)
                .map(WorkMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}