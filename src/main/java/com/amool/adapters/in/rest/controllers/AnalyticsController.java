package com.amool.adapters.in.rest.controllers;

import java.util.List;

import com.amool.adapters.in.rest.dtos.*;
import com.amool.application.usecases.*;
import com.amool.domain.model.ReadingHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import com.amool.adapters.in.rest.mappers.AnalyticsLikeChapterMapper;
import com.amool.adapters.in.rest.mappers.AnalyticsLikeWorkMapper;
import com.amool.adapters.in.rest.mappers.AnalyticsRatingWorkMapper;
import com.amool.adapters.in.rest.mappers.AnalyticsSuscribersPerAuthorMapper;
import com.amool.adapters.in.rest.mappers.AnalyticsSuscribersPerWorkMapper;
import com.amool.adapters.in.rest.mappers.SavedWorkMapper;
import com.amool.domain.model.AnalyticsRetention;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final GetLikesPerWorkUseCase getLikesPerWorkUseCase;
    private final GetLikesPerChapterUseCase getLikesPerChapterUseCase;
    private final GetRatingsPerWorkUseCase getRatingPerWorkUseCase;
    private final GetSavesPerWorkUseCase getSavesPerWorkUseCase;
    private final GetTotalPerAuthorUseCase getTotalPerAuthorUseCase;
    private final GetTotalPerWorkUseCase getTotalPerWorkUseCase;
    private final GetTotalSuscribersUseCase getTotalSuscribersUseCase;
    private final GetSuscribersPerWorkUseCase getSuscribersPerWorkUseCase;
    private final GetSuscribersPerAuthorUseCase getSuscribersPerAuthorUseCase;
    private final GetTotalRetention getTotalRetention;
    private final ObtainReadingHistory obtainReadingHistory;

    public AnalyticsController(GetLikesPerWorkUseCase getLikesPerWorkUseCase,
                               GetLikesPerChapterUseCase getLikesPerChapterUseCase,
                               GetRatingsPerWorkUseCase getRatingPerWorkUseCase,
                               GetSavesPerWorkUseCase getSavesPerWorkUseCase,
                               GetTotalPerAuthorUseCase getTotalPerAuthorUseCase,
                               GetTotalPerWorkUseCase getTotalPerWorkUseCase,
                               GetTotalSuscribersUseCase getTotalSuscribersUseCase,
                               GetSuscribersPerWorkUseCase getSuscribersPerWorkUseCase,
                               GetSuscribersPerAuthorUseCase getSuscribersPerAuthorUseCase,
                               GetTotalRetention getTotalRetention,
                               ObtainReadingHistory obtainReadingHistory) {
        this.obtainReadingHistory = obtainReadingHistory;
        this.getLikesPerWorkUseCase = getLikesPerWorkUseCase;
        this.getLikesPerChapterUseCase = getLikesPerChapterUseCase;
        this.getRatingPerWorkUseCase = getRatingPerWorkUseCase;
        this.getSavesPerWorkUseCase = getSavesPerWorkUseCase;
        this.getTotalPerAuthorUseCase = getTotalPerAuthorUseCase;
        this.getTotalPerWorkUseCase = getTotalPerWorkUseCase;
        this.getTotalSuscribersUseCase = getTotalSuscribersUseCase;
        this.getSuscribersPerWorkUseCase = getSuscribersPerWorkUseCase;
        this.getSuscribersPerAuthorUseCase = getSuscribersPerAuthorUseCase;
        this.getTotalRetention = getTotalRetention;
    }

    @GetMapping("/likesPerWork/{workId}")
    public ResponseEntity<List<AnalyticsLikeWorkDto>> getLikesPerWork(@PathVariable Long workId) {

        List<AnalyticsLikeWorkDto> likesPerWork = getLikesPerWorkUseCase.execute(workId).stream().map(AnalyticsLikeWorkMapper::toDto).collect(toList());

        return ResponseEntity.ok().body(likesPerWork);
    }

    @GetMapping("/likesPerChapter/{chapterId}")
    public ResponseEntity<List<AnalyticsLikeChapterDto>> getLikesPerChapter(@PathVariable Long chapterId) {
        List<AnalyticsLikeChapterDto> likesPerChapter = getLikesPerChapterUseCase.execute(chapterId).stream().map(AnalyticsLikeChapterMapper::toDto).collect(toList());
        return ResponseEntity.ok().body(likesPerChapter);
    }

    @GetMapping("/ratingPerWork/{workId}")
    public ResponseEntity<List<AnalyticsRatingWorkDto>> getRatingPerWork(@PathVariable Long workId) {
        List<AnalyticsRatingWorkDto> ratingsPerWork = getRatingPerWorkUseCase.execute(workId).stream().map(AnalyticsRatingWorkMapper::toDto).collect(toList());
        return ResponseEntity.ok().body(ratingsPerWork);
    }

    @GetMapping("/savesPerWork/{workId}")
    public ResponseEntity<List<AnalyticsSavedWorkDto>> getSavesPerWork(@PathVariable Long workId) {
        List<AnalyticsSavedWorkDto> savesPerWork = getSavesPerWorkUseCase.execute(workId).stream().map(SavedWorkMapper::toDto).collect(toList());
        return ResponseEntity.ok().body(savesPerWork);
    }

    @GetMapping("/totalSuscribers/{authorId}")
    public ResponseEntity<Long> getTotalSuscribers(@PathVariable Long authorId) {
        return ResponseEntity.ok().body(getTotalSuscribersUseCase.execute(authorId));
    }

    @GetMapping("/totalSuscribersPerAuthor/{authorId}")
    public ResponseEntity<Long> getTotalSuscribersPerAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok().body(getTotalPerAuthorUseCase.execute(authorId));
    }

    @GetMapping("/totalSuscribersPerWork/{workId}")
    public ResponseEntity<Long> getTotalSuscribersPerWork(@PathVariable Long workId) {
        return ResponseEntity.ok().body(getTotalPerWorkUseCase.execute(workId));
    }

    @GetMapping("/listSuscribersPerWork/{workId}")
    public ResponseEntity<List<AnalyticsSuscribersPerWorkDto>> getListSuscribersPerWork(@PathVariable Long workId) {
        List<AnalyticsSuscribersPerWorkDto> suscribersPerWork = getSuscribersPerWorkUseCase.execute(workId).stream().map(AnalyticsSuscribersPerWorkMapper::toDto).collect(toList());

        return ResponseEntity.ok().body(suscribersPerWork);
    }

    @GetMapping("/listSuscribersPerAuthor/{authorId}")
    public ResponseEntity<List<AnalyticsSuscribersPerAuthorDto>> getListSuscribersPerAuthor(@PathVariable Long authorId) {
        List<AnalyticsSuscribersPerAuthorDto> suscribersPerAuthor = getSuscribersPerAuthorUseCase.execute(authorId).stream().map(AnalyticsSuscribersPerAuthorMapper::toDto).collect(toList());

        return ResponseEntity.ok().body(suscribersPerAuthor);
    }

    @GetMapping("/readingPerChapter/{chapterId}")
    public ResponseEntity<List<AnalyticsReadingChapterDto>> getReadingPerChapter(@PathVariable Long chapterId){

        List<AnalyticsReadingChapterDto>  result = this.obtainReadingHistory.execute(chapterId)
                                                .stream()
                                                .map(ReadingHistory::toDto).toList();

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/readerRetention/{workId}")
    public ResponseEntity<List<AnalyticsRetentionDto>> getReaderRetention(@PathVariable Long workId){
        
        List<AnalyticsRetentionDto> result = this.getTotalRetention.execute(workId)
                                                .stream()
                                                .map(AnalyticsRetention::toDto).toList();

        return ResponseEntity.ok().body(result);
    }
}
