package com.amool.application.usecase;

import com.amool.application.port.out.TagPort;
import com.amool.domain.model.Tag;
import com.amool.application.usecases.GetMatchTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetMatchTagsTest {

    private TagPort tagPort;
    private GetMatchTags useCase;

    @BeforeEach
    public void setUp() {
        tagPort = Mockito.mock(TagPort.class);
        useCase = new GetMatchTags(tagPort);
    }

    @Test
    public void when_TagsExist_ThenReturnExistingTags() {
        Set<String> tagNames = Set.of("tag1", "tag2");
        Tag existingTag1 = new Tag(1L, "tag1");
        Tag existingTag2 = new Tag(2L, "tag2");

        when(tagPort.searchTag("tag1")).thenReturn(java.util.Optional.of(existingTag1));
        when(tagPort.searchTag("tag2")).thenReturn(java.util.Optional.of(existingTag2));

        Set<Tag> result = useCase.execute(tagNames);

        assertEquals(2, result.size());
        assertTrue(result.contains(existingTag1));
        assertTrue(result.contains(existingTag2));
        Mockito.verify(tagPort, Mockito.never()).createTag(anyString());
    }

    @Test
    public void when_TagsDontExist_ThenCreateAndReturnNewTags() {
        Set<String> tagNames = Set.of("newTag1", "newTag2");

        when(tagPort.searchTag("newTag1")).thenReturn(java.util.Optional.empty());
        when(tagPort.searchTag("newTag2")).thenReturn(java.util.Optional.empty());
        when(tagPort.createTag("newTag1")).thenReturn(1L);
        when(tagPort.createTag("newTag2")).thenReturn(2L);

        Set<Tag> result = useCase.execute(tagNames);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("newTag1")));
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("newTag2")));
        Mockito.verify(tagPort).createTag("newTag1");
        Mockito.verify(tagPort).createTag("newTag2");
    }

    @Test
    public void when_MixedTags_ThenReturnExistingAndNewTags() {
        Set<String> tagNames = Set.of("existingTag", "newTag");
        Tag existingTag = new Tag(1L, "existingTag");

        when(tagPort.searchTag("existingTag")).thenReturn(java.util.Optional.of(existingTag));
        when(tagPort.searchTag("newTag")).thenReturn(java.util.Optional.empty());
        when(tagPort.createTag("newTag")).thenReturn(2L);

        Set<Tag> result = useCase.execute(tagNames);

        assertEquals(2, result.size());
        assertTrue(result.contains(existingTag));
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("newTag") && t.getId() == 2L));
        Mockito.verify(tagPort).createTag("newTag");
    }
}
