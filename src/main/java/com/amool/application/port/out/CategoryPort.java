package com.amool.application.port.out;

import java.util.List;
import java.util.Optional;

import com.amool.domain.model.Category;

public interface CategoryPort {
    
    List<Category> findAllCategories();

    Optional<Category> getCategoryById(Long categoryId);
    
}
