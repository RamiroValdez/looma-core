package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.GetAllWorksUseCase;
import com.amool.application.usecases.ToggleWorkLikeUseCase;
import com.amool.domain.model.Work;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amool.security.JwtUserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/works")
@PreAuthorize("isAuthenticated()")
public class WorkController {

    private final GetAllWorksUseCase getAllWorksUseCase;
    private final ToggleWorkLikeUseCase toggleWorkLikeUseCase;

    public WorkController(GetAllWorksUseCase getAllWorksUseCase,
                          ToggleWorkLikeUseCase toggleWorkLikeUseCase) {
        this.getAllWorksUseCase = getAllWorksUseCase;
        this.toggleWorkLikeUseCase = toggleWorkLikeUseCase;
    }

    @GetMapping
    public ResponseEntity<List<WorkResponseDto>> getAllWorks() {
        List<Work> works = getAllWorksUseCase.execute();
        List<WorkResponseDto> response = works.stream()
                .map(WorkMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workId}/like")
    public ResponseEntity<LikeResponseDto> toggleWorkLike(
            @PathVariable Long workId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();
        LikeResponseDto response = toggleWorkLikeUseCase.execute(workId, userId);
        return ResponseEntity.ok(response);
    }

}
