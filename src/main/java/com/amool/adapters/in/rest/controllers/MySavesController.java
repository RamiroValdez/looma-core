package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.SaveWorkResponseDto;
import com.amool.application.usecases.GetSavedWorks;
import com.amool.application.usecases.IsWorkSaved;
import com.amool.application.usecases.ToggleSaveWork;
import com.amool.domain.model.Work;
import com.amool.security.JwtUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-works")
public class MySavesController {

    private final ToggleSaveWork toggleSaveWork;
    private final IsWorkSaved isWorkSaved;
    private final GetSavedWorks getSavedWorks;

    public MySavesController(
            ToggleSaveWork toggleSaveWork,
            IsWorkSaved isWorkSaved,
            GetSavedWorks getSavedWorks) {
        this.toggleSaveWork = toggleSaveWork;
        this.isWorkSaved = isWorkSaved;
        this.getSavedWorks = getSavedWorks;
    }

    @PostMapping("/{workId}/toggle")
    public ResponseEntity<?> toggleSaveWork(
            @AuthenticationPrincipal JwtUserPrincipal userDetails,
            @PathVariable Long workId) {
        
        Long userId = userDetails.getUserId();
        toggleSaveWork.execute(userId, workId);
        
        boolean isSaved = isWorkSaved.execute(userId, workId);
        return ResponseEntity.ok().body(
            new SaveWorkResponseDto(workId, isSaved)
        );
    }

    @GetMapping("/{workId}/status")
    public ResponseEntity<?> getSaveStatus(
            @AuthenticationPrincipal JwtUserPrincipal userDetails,
            @PathVariable Long workId) {
        
        Long userId = userDetails.getUserId();
        boolean isSaved = isWorkSaved.execute(userId, workId);
        
        return ResponseEntity.ok().body(
            new SaveWorkResponseDto(workId, isSaved)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<Work>> getSavedWorks(
            @AuthenticationPrincipal JwtUserPrincipal userDetails) {
        
        Long userId = userDetails.getUserId();
        List<Work> savedWorks = getSavedWorks.execute(userId);
        
        return ResponseEntity.ok(savedWorks);
    }
}
