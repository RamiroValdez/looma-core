package com.amool.application.port.out;

import java.util.List;

public interface UserPreferencesPort {
    void setPreferredCategories(Long userId, List<Long> categoryIds);
}
