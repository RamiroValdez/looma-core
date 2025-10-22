package com.amool.application.usecase;

import com.amool.application.port.out.TagSuggestionPort;
import com.amool.application.usecases.SuggestTagsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SuggestTagsUseCaseTest {

    private TagSuggestionPort tagSuggestionPort;
    private SuggestTagsUseCase useCase;

    @BeforeEach
    public void setUp() {
        tagSuggestionPort = Mockito.mock(TagSuggestionPort.class);
        useCase = new SuggestTagsUseCase(tagSuggestionPort);
    }

    @Test
    public void when_ValidInput_ThenReturnSuggestedTags() {
        String description = "A beautiful landscape with mountains and rivers";
        String title = "Mountain View";
        Set<String> existingTags = new HashSet<>(Arrays.asList("nature"));
        List<String> expectedTags = Arrays.asList("landscape", "mountains", "rivers");

        when(tagSuggestionPort.getSuggestedTags(
            eq(description.trim()),
            eq(title.trim()),
            anySet()
        )).thenReturn(expectedTags);

        List<String> result = useCase.execute(description, title, existingTags);

        assertEquals(expectedTags, result);
        verify(tagSuggestionPort).getSuggestedTags(
            description.trim(),
            title.trim(),
            Collections.singleton("nature")
        );
    }

    @Test
    public void when_NullDescription_ThenReturnEmptyList() {
        List<String> result = useCase.execute(null, "Title", Collections.emptySet());
        assertTrue(result.isEmpty());
        verify(tagSuggestionPort, Mockito.never()).getSuggestedTags(any(), any(), any());
    }

    @Test
    public void when_NullTitleButValidDescription_ThenReturnSuggestedTags() {
        String description = "A beautiful landscape with mountains and rivers";
        List<String> expectedTags = Arrays.asList("landscape", "mountains", "rivers");

        when(tagSuggestionPort.getSuggestedTags(
            eq(description.trim()),
            isNull(),
            eq(Collections.emptySet())
        )).thenReturn(expectedTags);

        List<String> result = useCase.execute(description, null, Collections.emptySet());

        assertEquals(expectedTags, result);
        verify(tagSuggestionPort).getSuggestedTags(
            description.trim(),
            null,
            Collections.emptySet()
        );
    }

    @Test
    public void when_NullTitleAndNoExistingTags_ThenReturnSuggestedTags() {
        String description = "A beautiful landscape with mountains and rivers";
        List<String> expectedTags = Arrays.asList("landscape", "mountains", "rivers");

        when(tagSuggestionPort.getSuggestedTags(
            eq(description.trim()),
            isNull(),
            eq(Collections.emptySet())
        )).thenReturn(expectedTags);

        List<String> result = useCase.execute(description, null, null);

        assertEquals(expectedTags, result);
        verify(tagSuggestionPort).getSuggestedTags(
            description.trim(),
            null,
            Collections.emptySet()
        );
    }
}
