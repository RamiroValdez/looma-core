package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.*;
import com.amool.application.usecases.GetUserRating;
import com.amool.application.usecases.GetWorkRatings;
import com.amool.application.usecases.RateWork;
import com.amool.security.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/works/{workId}/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "APIs para gestionar las calificaciones de las obras")
public class RatingController {

    private final RateWork rateWork;
    private final GetUserRating getUserRating;
    private final GetWorkRatings getWorkRatings;

    @Operation(summary = "Calificar una obra", 
              description = "Califica una obra con un valor entre 0.5 y 5.0")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Obra calificada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Valor de calificación inválido"),
        @ApiResponse(responseCode = "404", description = "Obra no encontrada")
    })
    @PostMapping
    public ResponseEntity<RatingResponse> rateWork(
            @Parameter(description = "ID de la obra a calificar") 
            @PathVariable Long workId,
            @Valid @RequestBody RateWorkRequest request,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        Long userId = userPrincipal.getUserId();
        Double ratingValue = request.getRating();
        
        Double averageRating = rateWork.execute(workId, userId, ratingValue);
        
        RatingResponse response = new RatingResponse(
            workId,
            userId,
            ratingValue,
            averageRating
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Integer> getWorkRatings(
            @Parameter(description = "ID de la obra") 
            @PathVariable Long workId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Integer workRatings = getWorkRatings.execute(workId, pageable);
        
        return ResponseEntity.ok(workRatings);
    }

    @GetMapping("/me")
    public ResponseEntity<Double> getUserRating(
            @Parameter(description = "ID de la obra")
            @PathVariable Long workId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {

        Optional<Double> result = getUserRating.execute(workId, userPrincipal.getUserId());

        return ResponseEntity.ok(result.orElse(null));
    }
}