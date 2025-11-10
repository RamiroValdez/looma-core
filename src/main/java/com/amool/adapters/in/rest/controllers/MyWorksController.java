package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.mappers.WorkMapper;
import com.amool.application.usecases.GetAuthenticatedUserWorksUseCase;
import com.amool.application.usecases.UpdateBannerUseCase;
import com.amool.application.usecases.UpdateCoverUseCase;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;

import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import com.amool.domain.exception.UnauthorizedAccessException;

@RestController
@RequestMapping("/api/my-works")
public class MyWorksController {

    private final GetAuthenticatedUserWorksUseCase getAuthenticatedUserWorksUseCase;
    private final UpdateCoverUseCase updateCoverUseCase;
    private final UpdateBannerUseCase updateBannerUseCase;

    public MyWorksController(
            GetAuthenticatedUserWorksUseCase getAuthenticatedUserWorksUseCase,
            UpdateCoverUseCase updateCoverUseCase,
            UpdateBannerUseCase updateBannerUseCase) {
        this.getAuthenticatedUserWorksUseCase = getAuthenticatedUserWorksUseCase;
        this.updateCoverUseCase = updateCoverUseCase;
        this.updateBannerUseCase = updateBannerUseCase;
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated() and #userId == principal.userId")
    public ResponseEntity<List<WorkResponseDto>> getWorksByUserId(
            @PathVariable Long userId, 
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        try {
            List<Work> works = getAuthenticatedUserWorksUseCase.execute(principal.getUserId());
            List<WorkResponseDto> response = works.stream()
                    .map(WorkMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new UnauthorizedAccessException("No se pudo obtener las obras del usuario autenticado: " + e.getMessage(), e);
        }
    }

    @PatchMapping(value = "/{workId}/cover", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateCover(
            @PathVariable("workId") Long workId,
            @RequestPart(value = "coverIaUrl", required = false) String optionalData,
            @RequestPart(value = "cover", required = false) MultipartFile coverFile,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        try {
            this.updateCoverUseCase.execute(workId, coverFile, principal.getUserId(),optionalData);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }   catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping(value = "/{workId}/banner", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateBanner(
            @PathVariable("workId") Long workId,
            @RequestPart("banner") MultipartFile bannerFile,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        try {
            this.updateBannerUseCase.execute(workId, bannerFile, principal.getUserId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}