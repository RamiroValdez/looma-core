package com.amool.application.usecases;

import com.amool.application.port.out.WorkPort;
import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.TagPort;
import com.amool.application.port.out.CategoryPort;
import com.amool.domain.model.*;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class UpdateWork {
    private final WorkPort workPort;
    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final TagPort tagPort;
    private final CategoryPort categoryPort;

    public UpdateWork(WorkPort workPort,
                      ObtainWorkByIdPort obtainWorkByIdPort,
                      TagPort tagPort,
                      CategoryPort categoryPort) {
        this.workPort = workPort;
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.tagPort = tagPort;
        this.categoryPort = categoryPort;
    }

    public Boolean execute(Long workId,
                        BigDecimal price,
                        Set<String> tagIds,
                        Set<Long> categoryIds,
                        String state) throws IllegalStateException {
        Work work = obtainWorkByIdPort.obtainWorkById(workId)
                .orElseThrow(() -> new IllegalStateException("Error al actualizar la obra"));

        try {
            if(price != null){
                work.setPrice(price);
            }
            
            if (tagIds != null) {
                work.setTags(loadOrCreateTags(tagIds));
            }
            
            if (state != null) {
                work.setState(state);
            }
            
            if (categoryIds != null) {
                work.setCategories(new ArrayList<>(loadOrCreateCategories(categoryIds)));
            }
            
            boolean updated = workPort.updateWork(work);
            if (!updated) {
                throw new IllegalStateException("Failed to update work");
            }

            return updated;
            
        } catch (Exception e) {
            throw new RuntimeException("Error updating work: " + e.getMessage(), e);
        }
    }

    private Set<Tag> loadOrCreateTags(Set<String> tagNames) throws IllegalStateException {
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

    private Set<Category> loadOrCreateCategories(Set<Long> categoryIds) throws IllegalStateException {
        Set<Category> categories = new HashSet<>();

        for (Long categoryId : categoryIds) {
            Category category = categoryPort.getCategoryById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));
            categories.add(category);
        }
        return categories;
    }
}
