package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.TagService;
import com.amool.hexagonal.application.port.out.TagPort;
import com.amool.hexagonal.domain.model.Tag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagPort tagPort;


    public TagServiceImpl(TagPort tagPort) {
        this.tagPort = tagPort;
    }

    @Override
    public Set<Tag> getMatchTags(Set<String> tagNames) {

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
