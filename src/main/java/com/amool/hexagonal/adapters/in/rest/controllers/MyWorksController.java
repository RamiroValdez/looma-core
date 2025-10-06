package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.WorkMapper;
import com.amool.hexagonal.application.port.in.WorkService;
import com.amool.hexagonal.domain.model.Work;
import com.amool.hexagonal.security.JwtUserPrincipal;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.amool.hexagonal.domain.exception.UnauthorizedAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my-works")
public class MyWorksController {

    private WorkService workService;

    public MyWorksController(WorkService workService) {
        this.workService = workService;
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and #userId == principal.userId")
    public ResponseEntity<List<WorkResponseDto>> getWorksByUserId(
            @PathVariable Long userId, 
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        try {
            List<Work> works = workService.getAuthenticatedUserWorks(principal.getUserId());
            List<WorkResponseDto> response = works.stream()
                    .map(WorkMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new UnauthorizedAccessException("No se pudo obtener las obras del usuario autenticado: " + e.getMessage(), e);
        }
    }

}