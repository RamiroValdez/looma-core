package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.TagEntity;
import com.amool.hexagonal.domain.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {

    public static Tag toDomain(TagEntity entity) {
        if (entity == null) {
            return null;
        }
        Tag tag = new Tag();
        tag.setId(entity.getId());
        tag.setName(entity.getName());
        return tag;
    }

    public static List<Tag> toDomainList(List<TagEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(TagMapper::toDomain)
                .collect(Collectors.toList());
    }

}
