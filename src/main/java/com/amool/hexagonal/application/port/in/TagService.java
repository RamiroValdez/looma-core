package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Tag;

import java.util.Set;

public interface TagService {

    Set<Tag> getMatchTags(Set<String> tagNames);

}
