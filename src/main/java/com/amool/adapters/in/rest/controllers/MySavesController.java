package com.amool.adapters.in.rest.controllers;

import com.amool.application.usecases.SaveWorkUseCase;
import com.amool.domain.model.Work;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long workId) {
        
        Long userId = Long.parseLong(userDetails.getUsername());
        saveWorkUseCase.toggleSaveWork(userId, workId);
        
        boolean isSaved = saveWorkUseCase.isWorkSavedByUser(userId, workId);
        return ResponseEntity.ok().body(
            new SaveWorkResponse(workId, isSaved)
        );
    }

    @GetMapping("/{workId}/status")
    public ResponseEntity<?> getSaveStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long workId) {
        
        Long userId = Long.parseLong(userDetails.getUsername());
        boolean isSaved = saveWorkUseCase.isWorkSavedByUser(userId, workId);
        
        return ResponseEntity.ok().body(
            new SaveWorkResponse(workId, isSaved)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<Work>> getSavedWorks(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Work> savedWorks = saveWorkUseCase.getSavedWorksByUser(userId);
        
        return ResponseEntity.ok(savedWorks);
    }

    private record SaveWorkResponse(Long workId, boolean isSaved) {}
}
