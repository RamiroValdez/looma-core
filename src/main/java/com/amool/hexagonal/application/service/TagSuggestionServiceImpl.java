package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.TagSuggestionService;
import com.amool.hexagonal.application.port.out.TagSuggestionPort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagSuggestionServiceImpl implements TagSuggestionService {

    private final TagSuggestionPort tagSuggestionPort;

    public TagSuggestionServiceImpl(TagSuggestionPort tagSuggestionPort) {
        this.tagSuggestionPort = tagSuggestionPort;
    }

    @Override
    public List<String> suggestTags(String description, String title, Set<String> existingTags) {
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
