package com.amool.hexagonal.application.service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import com.amool.hexagonal.application.port.in.CategoryService;
import com.amool.hexagonal.application.port.out.CategoryPort;
import com.amool.hexagonal.domain.model.Category;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryPort categoryPort;
    
    public CategoryServiceImpl(CategoryPort categoryPort) {
        this.categoryPort = categoryPort;
    }

    @Override
    public Optional<List<Category>> obtainAllCategories() {
        List<Category> categories = categoryPort.findAllCategories();
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories);
    }
    
}
