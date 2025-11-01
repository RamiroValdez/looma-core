package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.LikeResponseDto;
import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.GetAllWorksUseCase;
import com.amool.application.usecases.LikeWorkUseCase;
import com.amool.application.usecases.UnlikeWorkUseCase;
import com.amool.domain.model.Work;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amool.security.JwtUserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class WorkController {

    private final GetAllWorksUseCase getAllWorksUseCase;
    private final LikeWorkUseCase likeWorkUseCase;
    private final UnlikeWorkUseCase unlikeWorkUseCase;
    private final WorkMapper workMapper;

    public WorkController(GetAllWorksUseCase getAllWorksUseCase, WorkMapper workMapper) {
        this.getAllWorksUseCase = getAllWorksUseCase;
        this.workMapper = workMapper;
    }

    @GetMapping
    public ResponseEntity<List<WorkResponseDto>> getAllWorks() {
        List<Work> works = getAllWorksUseCase.execute();
        List<WorkResponseDto> response = works.stream()
                .map(WorkMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/work/{workId}/like")
    public ResponseEntity<LikeResponseDto> likeWork(
            @PathVariable Long workId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();
        LikeResponseDto response = likeWorkUseCase.execute(workId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/work/{workId}/like")
    public ResponseEntity<LikeResponseDto> unlikeWork(
            @PathVariable Long workId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();
        LikeResponseDto response = unlikeWorkUseCase.execute(workId, userId);
        return ResponseEntity.ok(response);
    }

}
