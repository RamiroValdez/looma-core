package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Tag;

import java.util.Optional;

public interface TagPort {

    Optional<Tag> searchTag(String tagName);

    Long createTag(String tagName);

}
