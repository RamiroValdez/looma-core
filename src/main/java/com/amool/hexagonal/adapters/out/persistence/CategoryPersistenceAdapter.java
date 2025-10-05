package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.CategoryEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.CategoryMapper;
import com.amool.hexagonal.application.port.out.CategoryPort;
import com.amool.hexagonal.domain.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryPersistenceAdapter implements CategoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        List<CategoryEntity> categoryEntities = entityManager
                .createQuery("SELECT c FROM CategoryEntity c ORDER BY c.name", CategoryEntity.class)
                .getResultList();
        
        return categoryEntities.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }
}
