package com.amool.application.usecases;

import com.amool.application.port.out.UserPreferencesPort;

import java.util.List;

public class SetUserPreferences {

    private final UserPreferencesPort userPreferencesPort;

    public SetUserPreferences(UserPreferencesPort userPreferencesPort) {
        this.userPreferencesPort = userPreferencesPort;
    }

    public void execute(Long userId, List<Long> categoryIds) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (categoryIds == null) throw new IllegalArgumentException("categoryIds is required");
        userPreferencesPort.setPreferredCategories(userId, categoryIds);
    }
}
