package com.amool.domain.model;

import java.util.Objects;

public class Rating {
    private static final double MIN_RATING = 0.5;
    private static final double MAX_RATING = 5.0;
    private static final double RATING_STEP = 0.5;

    private final double value;

    public Rating(double value) {
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new IllegalArgumentException("Rating must be between " + MIN_RATING + " and " + MAX_RATING);
        }
        
        this.value = Math.round(value * 2) / 2.0;
    }

    public double getValue() {
        return value;
    }

    public static boolean isValid(double value) {
        return value >= MIN_RATING && value <= MAX_RATING && (value * 2) % 1 <= 0.01;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Double.compare(rating.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
