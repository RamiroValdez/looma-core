package com.amool.hexagonal.application.port.out;

import java.util.List;
import java.util.Optional;

import com.amool.hexagonal.domain.model.Category;

public interface CategoryPort {
    
    List<Category> findAllCategories();

    Optional<Category> getCategoryById(Long categoryId);
    
}
