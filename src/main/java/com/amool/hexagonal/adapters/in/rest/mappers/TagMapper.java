package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.TagDto;
import com.amool.hexagonal.domain.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {

    public static TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    public static List<TagDto> toDtoList(List<Tag> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toList());
    }
}
