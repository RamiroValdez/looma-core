package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.SaveWorkResponseDto;
import com.amool.application.usecases.SaveWorkUseCase;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-works")
public class MySavesController {

    private final SaveWorkUseCase saveWorkUseCase;

    public MySavesController(SaveWorkUseCase saveWorkUseCase) {
        this.saveWorkUseCase = saveWorkUseCase;
    }

    @PostMapping("/{workId}/toggle")
    public ResponseEntity<?> toggleSaveWork(
            @AuthenticationPrincipal JwtUserPrincipal userDetails,
            @PathVariable Long workId) {
        
        Long userId = userDetails.getUserId();
        saveWorkUseCase.toggleSaveWork(userId, workId);
        
        boolean isSaved = saveWorkUseCase.isWorkSavedByUser(userId, workId);
        return ResponseEntity.ok().body(
            new SaveWorkResponseDto(workId, isSaved)
        );
    }

    @GetMapping("/{workId}/status")
    public ResponseEntity<?> getSaveStatus(
            @AuthenticationPrincipal JwtUserPrincipal userDetails,
            @PathVariable Long workId) {
        
        Long userId = userDetails.getUserId();
        boolean isSaved = saveWorkUseCase.isWorkSavedByUser(userId, workId);
        
        return ResponseEntity.ok().body(
            new SaveWorkResponseDto(workId, isSaved)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<Work>> getSavedWorks(
            @AuthenticationPrincipal JwtUserPrincipal userDetails) {
        
        Long userId = userDetails.getUserId();
        List<Work> savedWorks = saveWorkUseCase.getSavedWorksByUser(userId);
        
        return ResponseEntity.ok(savedWorks);
    }
}
