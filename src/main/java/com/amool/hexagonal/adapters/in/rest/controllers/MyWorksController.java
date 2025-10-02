package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.WorkMapper;
import com.amool.hexagonal.application.port.in.ObtainWorkByIdUseCase;
import com.amool.hexagonal.domain.model.Work;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my-works")
public class MyWorksController {

    private ObtainWorkByIdUseCase obtainWorkByIdUseCase;

    public MyWorksController(ObtainWorkByIdUseCase obtainWorkByIdUseCase) {
        this.obtainWorkByIdUseCase = obtainWorkByIdUseCase;
    }

    @GetMapping("/{userId}")
    public List<WorkResponseDto> getWorksByUserId(@PathVariable Long userId) {
        List<Work> works = obtainWorkByIdUseCase.getWorksByUserId(userId);
        return works.stream()
                .map(WorkMapper::toDto)
                .collect(Collectors.toList());
    }

}