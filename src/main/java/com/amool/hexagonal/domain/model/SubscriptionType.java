package com.amool.hexagonal.domain.model;

public enum SubscriptionType {
    CHAPTER,
    AUTHOR,
    WORK;

    public static SubscriptionType fromString(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase()) {
            case "chapter" -> CHAPTER;
            case "author" -> AUTHOR;
            case "work" -> WORK;
            default -> throw new IllegalArgumentException("Unknown subscription type: " + value);
        };
    }
}
