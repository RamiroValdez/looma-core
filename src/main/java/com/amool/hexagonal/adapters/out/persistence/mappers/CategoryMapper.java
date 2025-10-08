package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.adapters.out.persistence.entity.CategoryEntity;
import com.amool.hexagonal.domain.model.Category;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        Category category = new Category();
        category.setId(entity.getId());
        category.setName(entity.getName());
        return category;
    }

    public static List<Category> toDomainList(Set<CategoryEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static Set<CategoryEntity> toEntitySet(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(category -> {
                    CategoryEntity entity = new CategoryEntity();
                    entity.setId(category.getId());
                    entity.setName(category.getName());
                    return entity;
                })
                .collect(Collectors.toSet());
    }
}
