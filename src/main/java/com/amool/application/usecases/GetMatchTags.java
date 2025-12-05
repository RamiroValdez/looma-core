package com.amool.application.usecases;

import com.amool.application.port.out.TagPort;
import com.amool.domain.model.Tag;

import java.util.HashSet;
import java.util.Set;

public class GetMatchTags {

    private final TagPort tagPort;

    public GetMatchTags(TagPort tagPort) {
        this.tagPort = tagPort;
    }

    public Set<Tag> execute(Set<String> tagNames) {

        Set<Tag> tags = new HashSet<>();

        for (String tagName : tagNames) {
            Tag tag = tagPort.searchTag(tagName).orElseGet(() -> {
                Long tagId = tagPort.createTag(tagName);
                return new Tag(tagId, tagName);
            });
            tags.add(tag);
        }

        return tags;
    }
}
