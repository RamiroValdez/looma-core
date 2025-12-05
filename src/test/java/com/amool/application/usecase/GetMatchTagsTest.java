package com.amool.application.usecase;

import com.amool.application.port.out.TagPort;
import com.amool.application.usecases.GetMatchTags;
import com.amool.domain.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GetMatchTagsTest {

    private TagPort tagPort;
    private GetMatchTags useCase;

    @BeforeEach
    public void setUp() {
        tagPort = Mockito.mock(TagPort.class);
        useCase = new GetMatchTags(tagPort);
    }

    private void givenExistingTags(Map<String, Tag> existing) {
        existing.forEach((name, tag) -> when(tagPort.searchTag(name)).thenReturn(Optional.of(tag)));
    }

    private void givenNonExistingTags(Set<String> names) {
        names.forEach(name -> when(tagPort.searchTag(name)).thenReturn(Optional.empty()));
    }

    private void givenCreateTagWillReturnIds(Map<String, Long> ids) {
        ids.forEach((name, id) -> when(tagPort.createTag(name)).thenReturn(id));
    }

    private Set<Tag> whenGetMatchTags(Set<String> tagNames) {
        return useCase.execute(tagNames);
    }

    private void thenResultSizeIs(Set<Tag> result, int expected) {
        assertEquals(expected, result.size());
    }

    private void thenResultContainsAll(Set<Tag> result, Set<Tag> expectedTags) {
        expectedTags.forEach(tag -> assertTrue(result.contains(tag), "Debe contener tag: " + tag.getName()));
    }

    private void thenResultContainsNames(Set<Tag> result, Set<String> expectedNames) {
        Set<String> resultNames = result.stream().map(Tag::getName).collect(Collectors.toSet());
        expectedNames.forEach(n -> assertTrue(resultNames.contains(n), "Debe contener nombre: " + n));
    }

    private void thenTagCreated(String name) { verify(tagPort).createTag(name); }
    private void thenTagNotCreated(String name) { verify(tagPort, never()).createTag(name); }
    private void thenNoTagCreations() { verify(tagPort, never()).createTag(anyString()); }

    @Test
    public void when_TagsExist_ThenReturnExistingTags() {
        Tag existingTag1 = new Tag(1L, "tag1");
        Tag existingTag2 = new Tag(2L, "tag2");
        givenExistingTags(Map.of(
                "tag1", existingTag1,
                "tag2", existingTag2
        ));
        Set<String> tagNames = Set.of("tag1", "tag2");

        Set<Tag> result = whenGetMatchTags(tagNames);

        thenResultSizeIs(result, 2);
        thenResultContainsAll(result, Set.of(existingTag1, existingTag2));
        thenNoTagCreations();
    }

    @Test
    public void when_TagsDontExist_ThenCreateAndReturnNewTags() {
        Set<String> tagNames = Set.of("newTag1", "newTag2");
        givenNonExistingTags(tagNames);
        givenCreateTagWillReturnIds(Map.of("newTag1", 1L, "newTag2", 2L));

        Set<Tag> result = whenGetMatchTags(tagNames);

        thenResultSizeIs(result, 2);
        thenResultContainsNames(result, tagNames);
        thenTagCreated("newTag1");
        thenTagCreated("newTag2");
    }

    @Test
    public void when_MixedTags_ThenReturnExistingAndNewTags() {
        Tag existingTag = new Tag(1L, "existingTag");
        Set<String> tagNames = Set.of("existingTag", "newTag");
        givenExistingTags(Map.of("existingTag", existingTag));
        givenNonExistingTags(Set.of("newTag"));
        givenCreateTagWillReturnIds(Map.of("newTag", 2L));

        Set<Tag> result = whenGetMatchTags(tagNames);

        thenResultSizeIs(result, 2);
        assertTrue(result.contains(existingTag));
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("newTag") && t.getId() == 2L));
        thenTagCreated("newTag");
        thenTagNotCreated("existingTag");
    }
}
