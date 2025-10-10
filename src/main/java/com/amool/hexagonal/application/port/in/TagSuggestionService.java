package com.amool.hexagonal.application.port.in;

import java.util.List;
import java.util.Set;

public interface TagSuggestionService {

    List<String> suggestTags(String description, String title, Set<String> existingTags);
}
