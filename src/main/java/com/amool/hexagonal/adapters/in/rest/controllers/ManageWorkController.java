package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.adapters.in.rest.mappers.WorkMapper;
import com.amool.hexagonal.application.port.in.ObtainWorkByIdUseCase;
import com.amool.hexagonal.application.port.out.LoadWorkOwnershipPort;
import com.amool.hexagonal.security.JwtUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manage-work")
public class ManageWorkController {

    private ObtainWorkByIdUseCase obtainWorkByIdUseCase;
    private final LoadWorkOwnershipPort loadWorkOwnershipPort;

    public ManageWorkController(ObtainWorkByIdUseCase obtainWorkByIdUseCase,
                                LoadWorkOwnershipPort loadWorkOwnershipPort) {
        this.obtainWorkByIdUseCase = obtainWorkByIdUseCase;
        this.loadWorkOwnershipPort = loadWorkOwnershipPort;
    }

    @GetMapping("/{workId}")
    public ResponseEntity<WorkResponseDto> getWorkById(@PathVariable Long workId) {
        // Require authenticated user to be the owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isOwner = loadWorkOwnershipPort.isOwner(workId, principal.getUserId());
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return obtainWorkByIdUseCase.execute(workId)
                .map(WorkMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}