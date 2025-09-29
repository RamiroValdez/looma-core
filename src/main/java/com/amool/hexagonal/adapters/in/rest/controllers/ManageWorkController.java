package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
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
    public WorkResponseDto getWorkById(@PathVariable Long workId) {

        obtainWorkByIdUseCase.exectute(workId);

        WorkResponseDto dto = new WorkResponseDto();


        return dto;
    }

}