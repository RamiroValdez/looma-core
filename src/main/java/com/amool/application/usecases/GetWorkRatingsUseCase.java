package com.amool.application.usecases;

import com.amool.application.port.out.RatingPort;
import com.amool.application.port.out.RatingPort.RatingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class GetWorkRatingsUseCase {

    private final RatingPort ratingPort;

    public GetWorkRatingsUseCase(RatingPort ratingPort) {
        this.ratingPort = ratingPort;
    }

    public WorkRatings execute(Long workId, Pageable pageable) {
        Double averageRating = ratingPort.getAverageRating(workId);
        Page<RatingDto> ratingsPage = ratingPort.getWorkRatings(workId, pageable);
        
        return new WorkRatings(
            workId,
            averageRating,
            ratingsPage.getTotalElements(),
            ratingsPage.getContent()
        );
    }

    public static final class WorkRatings {
        private final Long workId;
        private final Double averageRating;
        private final long totalRatings;
        private final List<RatingDto> ratings;

        public WorkRatings(Long workId, Double averageRating, long totalRatings, List<RatingDto> ratings) {
            this.workId = workId;
            this.averageRating = averageRating;
            this.totalRatings = totalRatings;
            this.ratings = ratings;
        }

        public Long getWorkId() {
            return workId;
        }

        public Double getAverageRating() {
            return averageRating;
        }

        public long getTotalRatings() {
            return totalRatings;
        }

        public List<RatingDto> getRatings() {
            return ratings;
        }
    }
}
