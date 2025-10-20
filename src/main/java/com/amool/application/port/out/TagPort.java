package com.amool.application.port.out;

import com.amool.domain.model.Tag;

import java.util.Optional;

public interface TagPort {

    Optional<Tag> searchTag(String tagName);

    Long createTag(String tagName);

}
