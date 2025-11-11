package com.amool.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import com.amool.adapters.in.rest.dtos.AnalyticsLikeWorkDto;
import com.amool.adapters.in.rest.dtos.AnalyticsLikeChapterDto;
import com.amool.adapters.in.rest.mappers.AnalyticsLikeChapterMapper;
import com.amool.adapters.in.rest.mappers.AnalyticsLikeWorkMapper;
import com.amool.application.usecases.GetLikesPerChapterUseCase;
import com.amool.application.usecases.GetLikesPerWorkUseCase;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final GetLikesPerWorkUseCase getLikesPerWorkUseCase;
    private final GetLikesPerChapterUseCase getLikesPerChapterUseCase;

    public AnalyticsController(GetLikesPerWorkUseCase getLikesPerWorkUseCase, GetLikesPerChapterUseCase getLikesPerChapterUseCase) {
        this.getLikesPerWorkUseCase = getLikesPerWorkUseCase;
        this.getLikesPerChapterUseCase = getLikesPerChapterUseCase;
    }

    @GetMapping("/likesPerWork/{workId}")
    public ResponseEntity<List<AnalyticsLikeWorkDto>> getLikesPerWork(@PathVariable Long workId){

        List<AnalyticsLikeWorkDto> likesPerWork = getLikesPerWorkUseCase.execute(workId).stream().map(AnalyticsLikeWorkMapper::toDto).collect(Collectors.toList());

        return ResponseEntity.ok().body(likesPerWork);
    }

    @GetMapping("/likesPerChapter/{chapterId}")
    public ResponseEntity<List<AnalyticsLikeChapterDto>> getLikesPerChapter(@PathVariable Long chapterId){
        List<AnalyticsLikeChapterDto> likesPerChapter = getLikesPerChapterUseCase.execute(chapterId).stream().map(AnalyticsLikeChapterMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok().body(likesPerChapter);
    }

    @GetMapping("/ratingPerWork/{workId}")
    public ResponseEntity<?> getRatingPerWork(){
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/savesPerWork/{workId}")
    public ResponseEntity<?> getSavesPerWork(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/totalSuscribers")
    public ResponseEntity<?> getTotalSuscribers(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/totalSuscribersPerAuthor/{authorId}")
    public ResponseEntity<?> getTotalSuscribersPerAuthor(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/totalSuscribersPerWork/{workId}")
    public ResponseEntity<?> getTotalSuscribersPerWork(){
        return ResponseEntity.ok().build();
    }
}
