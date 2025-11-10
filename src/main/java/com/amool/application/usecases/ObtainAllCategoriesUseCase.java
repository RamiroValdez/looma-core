package com.amool.application.usecases;

import com.amool.application.port.out.CategoryPort;
import com.amool.domain.model.Category;

import java.util.List;
import java.util.Optional;

public class ObtainAllCategoriesUseCase {

    private final CategoryPort categoryPort;

    public ObtainAllCategoriesUseCase(CategoryPort categoryPort) {
        this.categoryPort = categoryPort;
    }

    public Optional<List<Category>> execute() {

        List<Category> categories = categoryPort.findAllCategories();

        return categories.isEmpty() ? Optional.empty() : Optional.of(categories);

    }

}
