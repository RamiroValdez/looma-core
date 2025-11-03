package com.amool.adapters.in.rest.controllers;

import com.amool.adapters.in.rest.dtos.*;
import com.amool.application.usecases.GetUserRatingUseCase;
import com.amool.application.usecases.GetWorkRatingsUseCase;
import com.amool.application.usecases.RateWorkUseCase;
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

@RestController
@RequestMapping("/api/works/{workId}/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "APIs para gestionar las calificaciones de las obras")
public class RatingController {

    private final RateWorkUseCase rateWorkUseCase;
    private final GetUserRatingUseCase getUserRatingUseCase;
    private final GetWorkRatingsUseCase getWorkRatingsUseCase;

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
        double ratingValue = request.getRating();
        
        double averageRating = rateWorkUseCase.execute(workId, userId, ratingValue);
        
        RatingResponse response = new RatingResponse(
            workId,
            userId,
            ratingValue,
            averageRating
        );
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener la calificación del usuario", 
              description = "Obtiene la calificación que el usuario autenticado le dio a una obra específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calificación encontrada"),
        @ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    @GetMapping("/me")
    public ResponseEntity<RatingResponse> getUserRating(
            @Parameter(description = "ID de la obra") 
            @PathVariable Long workId,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        
        return getUserRatingUseCase.execute(workId, userPrincipal.getUserId())
                .map(rating -> {
                    var workRatings = getWorkRatingsUseCase.execute(workId, Pageable.unpaged());
                    return new RatingResponse(
                        workId,
                        userPrincipal.getUserId(),
                        rating,
                        workRatings.getAverageRating()
                    );
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener todas las calificaciones de una obra", 
              description = "Obtiene una lista paginada de todas las calificaciones de una obra específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calificaciones obtenidas exitosamente")
    })
    @GetMapping
    public ResponseEntity<RatingListResponse> getWorkRatings(
            @Parameter(description = "ID de la obra") 
            @PathVariable Long workId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        var workRatings = getWorkRatingsUseCase.execute(workId, pageable);
        
        var ratings = workRatings.getRatings().stream()
                .map(dto -> new RatingListResponse.RatingDto(dto.userId(), dto.rating()))
                .toList();
        
        var response = new RatingListResponse(
            workId,
            workRatings.getAverageRating(),
            workRatings.getTotalRatings(),
            ratings
        );
        
        return ResponseEntity.ok(response);
    }
}