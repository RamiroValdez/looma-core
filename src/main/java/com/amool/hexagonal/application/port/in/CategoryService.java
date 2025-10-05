package com.amool.hexagonal.application.port.in;

import java.util.List;
import java.util.Optional;
import com.amool.hexagonal.domain.model.Category;

public interface CategoryService {
    
    Optional<List<Category>> obtainAllCategories();
    
}
