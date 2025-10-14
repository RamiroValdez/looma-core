package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.TagEntity;
import com.amool.hexagonal.domain.model.Tag;

import java.util.List;
import java.util.Set;
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

    public static Set<Tag> toDomainSet(Set<TagEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(TagMapper::toDomain)
                .collect(Collectors.toSet());
    }

    public static TagEntity toEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagEntity entity = new TagEntity();
        entity.setId(tag.getId());
        entity.setName(tag.getName());
        return entity;
    }

    public static Set<TagEntity> toEntitySet(Set<Tag> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(TagMapper::toEntity)
                .collect(Collectors.toSet());
    }

}
