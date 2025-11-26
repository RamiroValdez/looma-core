package com.amool.application.usecase;

import com.amool.application.port.out.TagSuggestionPort;
import com.amool.application.usecases.SuggestTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SuggestTagsTest {

    private static final String DESCRIPTION = "A beautiful landscape with mountains and rivers";
    private static final String TITLE = "Mountain View";
    private static final List<String> EXPECTED_TAGS = Arrays.asList("landscape", "mountains", "rivers");

    private TagSuggestionPort tagSuggestionPort;
    private SuggestTags useCase;

    @BeforeEach
    public void setUp() {
        tagSuggestionPort = Mockito.mock(TagSuggestionPort.class);
        useCase = new SuggestTags(tagSuggestionPort);
    }

    @Test
    public void shouldReturnSuggestedTagsWhenInputIsValid() {
        Set<String> existingTags = new HashSet<>(Collections.singleton("nature"));
        givenSuggestedTagsResponse(DESCRIPTION, TITLE, existingTags, EXPECTED_TAGS);

        List<String> result = whenSuggestingTags(DESCRIPTION, TITLE, existingTags);

        thenSuggestedTagsAre(result, EXPECTED_TAGS);
        thenSuggestionRequestedWith(DESCRIPTION.trim(), TITLE.trim(), Collections.singleton("nature"));
    }

    @Test
    public void shouldReturnEmptyListWhenDescriptionIsMissing() {
        List<String> result = whenSuggestingTags(null, TITLE, Collections.emptySet());

        thenSuggestedTagsAre(result, Collections.emptyList());
        thenNoSuggestionIsRequested();
    }

    @Test
    public void shouldReturnSuggestedTagsWhenTitleIsNullAndDescriptionExists() {
        givenSuggestedTagsResponse(DESCRIPTION, null, Collections.emptySet(), EXPECTED_TAGS);

        List<String> result = whenSuggestingTags(DESCRIPTION, null, Collections.emptySet());

        thenSuggestedTagsAre(result, EXPECTED_TAGS);
        thenSuggestionRequestedWith(DESCRIPTION.trim(), null, Collections.emptySet());
    }

    @Test
    public void shouldHandleNullExistingTagsWhenTitleIsNull() {
        givenSuggestedTagsResponse(DESCRIPTION, null, Collections.emptySet(), EXPECTED_TAGS);

        List<String> result = whenSuggestingTags(DESCRIPTION, null, null);

        thenSuggestedTagsAre(result, EXPECTED_TAGS);
        thenSuggestionRequestedWith(DESCRIPTION.trim(), null, Collections.emptySet());
    }

    private void givenSuggestedTagsResponse(String description, String title, Set<String> existingTags, List<String> response) {
        when(tagSuggestionPort.getSuggestedTags(
            eq(trimOrNull(description)),
            eq(trimOrNull(title)),
            eq(existingTags == null ? Collections.emptySet() : existingTags)
        )).thenReturn(response);
    }

    private List<String> whenSuggestingTags(String description, String title, Set<String> existingTags) {
        return useCase.execute(description, title, existingTags);
    }

    private void thenSuggestedTagsAre(List<String> result, List<String> expected) {
        assertEquals(expected, result);
    }

    private void thenSuggestionRequestedWith(String description, String title, Set<String> existingTags) {
        verify(tagSuggestionPort).getSuggestedTags(description, title, existingTags);
    }

    private void thenNoSuggestionIsRequested() {
        Mockito.verify(tagSuggestionPort, Mockito.never()).getSuggestedTags(any(), any(), any());
    }

    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
