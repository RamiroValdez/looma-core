package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.CategoryEntity;
import com.amool.adapters.out.persistence.mappers.CategoryMapper;
import com.amool.application.port.out.CategoryPort;
import com.amool.domain.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryPersistenceAdapter implements CategoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {
        List<CategoryEntity> categoryEntities = entityManager
                .createQuery("SELECT c FROM CategoryEntity c", CategoryEntity.class)
                .getResultList();
        
        return categoryEntities.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> getCategoryById(Long categoryId) {
        CategoryEntity categoryEntity = entityManager.find(CategoryEntity.class, categoryId);
        return Optional.ofNullable(CategoryMapper.toDomain(categoryEntity));
    }
}
