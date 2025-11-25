package com.amool.application.usecases;

import com.amool.application.port.out.TagSuggestionPort;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SuggestTags {

    private final TagSuggestionPort tagSuggestionPort;

    public SuggestTags(TagSuggestionPort tagSuggestionPort) {
        this.tagSuggestionPort = tagSuggestionPort;
    }

    public List<String> execute(String description, String title, Set<String> existingTags) {
        if (!StringUtils.hasText(description)) {
            return Collections.emptyList();
        }

        String normalizedTitle = StringUtils.hasText(title) ? title.trim() : null;
        Set<String> normalizedTags = existingTags == null ? Collections.emptySet() : existingTags.stream()
                .filter(StringUtils::hasText)
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return tagSuggestionPort.getSuggestedTags(description.trim(), normalizedTitle, normalizedTags);
    }
}
