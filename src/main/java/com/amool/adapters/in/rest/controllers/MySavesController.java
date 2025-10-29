package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.SaveWorkResponseDto;
import com.amool.application.usecases.GetSavedWorksUseCase;
import com.amool.application.usecases.IsWorkSavedUseCase;
import com.amool.application.usecases.ToggleSaveWorkUseCase;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-works")
public class MySavesController {

    private final ToggleSaveWorkUseCase toggleSaveWorkUseCase;
    private final IsWorkSavedUseCase isWorkSavedUseCase;
    private final GetSavedWorksUseCase getSavedWorksUseCase;

    public MySavesController(
            ToggleSaveWorkUseCase toggleSaveWorkUseCase,
            IsWorkSavedUseCase isWorkSavedUseCase,
            GetSavedWorksUseCase getSavedWorksUseCase) {
        this.toggleSaveWorkUseCase = toggleSaveWorkUseCase;
        this.isWorkSavedUseCase = isWorkSavedUseCase;
        this.getSavedWorksUseCase = getSavedWorksUseCase;
    }

    @PostMapping("/{workId}/toggle")
    public ResponseEntity<?> toggleSaveWork(
            @AuthenticationPrincipal JwtUserPrincipal userDetails,
            @PathVariable Long workId) {
        
        Long userId = userDetails.getUserId();
        toggleSaveWorkUseCase.execute(userId, workId);
        
        boolean isSaved = isWorkSavedUseCase.execute(userId, workId);
        return ResponseEntity.ok().body(
            new SaveWorkResponseDto(workId, isSaved)
        );
    }

    @GetMapping("/{workId}/status")
    public ResponseEntity<?> getSaveStatus(
            @AuthenticationPrincipal JwtUserPrincipal userDetails,
            @PathVariable Long workId) {
        
        Long userId = userDetails.getUserId();
        boolean isSaved = isWorkSavedUseCase.execute(userId, workId);
        
        return ResponseEntity.ok().body(
            new SaveWorkResponseDto(workId, isSaved)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<Work>> getSavedWorks(
            @AuthenticationPrincipal JwtUserPrincipal userDetails) {
        
        Long userId = userDetails.getUserId();
        List<Work> savedWorks = getSavedWorksUseCase.execute(userId);
        
        return ResponseEntity.ok(savedWorks);
    }
}
