package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/works")
public class WorkController {

    private final WorkPort workPort;
    private final WorkMapper workMapper;

    public WorkController(WorkPort workPort, WorkMapper workMapper) {
        this.workPort = workPort;
        this.workMapper = workMapper;
    }

    @GetMapping
    public ResponseEntity<List<WorkResponseDto>> getAllWorks() {
        List<Work> works = workPort.getAllWorks();
        List<WorkResponseDto> response = works.stream()
                .map(WorkMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
