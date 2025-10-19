package com.amool.application.port.out;

import java.util.List;
import java.util.Set;

public interface TagSuggestionPort {

    List<String> getSuggestedTags(String description, String title, Set<String> existingTags);
}
