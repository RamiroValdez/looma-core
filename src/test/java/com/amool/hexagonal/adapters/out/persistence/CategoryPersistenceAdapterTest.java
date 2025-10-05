package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.CategoryEntity;
import com.amool.hexagonal.domain.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CategoryPersistenceAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<CategoryEntity> typedQuery;

    @InjectMocks
    private CategoryPersistenceAdapter categoryPersistenceAdapter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findAllCategories_ShouldReturnCategories_WhenCategoriesExist() {
        // Arrange
        CategoryEntity entity1 = new CategoryEntity();
        entity1.setId(1L);
        entity1.setName("Fiction");

        CategoryEntity entity2 = new CategoryEntity();
        entity2.setId(2L);
        entity2.setName("Science");

        List<CategoryEntity> entities = Arrays.asList(entity1, entity2);

        when(entityManager.createQuery(anyString(), eq(CategoryEntity.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(entities);

        // Act
        List<Category> result = categoryPersistenceAdapter.findAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Fiction", result.get(0).getName());
        assertEquals("Science", result.get(1).getName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(CategoryEntity.class));
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void findAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(CategoryEntity.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = categoryPersistenceAdapter.findAllCategories();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager, times(1)).createQuery(anyString(), eq(CategoryEntity.class));
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void findAllCategories_ShouldReturnCategoriesOrderedByName() {
        // Arrange
        CategoryEntity entity1 = new CategoryEntity();
        entity1.setId(1L);
        entity1.setName("Adventure");

        CategoryEntity entity2 = new CategoryEntity();
        entity2.setId(2L);
        entity2.setName("Biography");

        CategoryEntity entity3 = new CategoryEntity();
        entity3.setId(3L);
        entity3.setName("Comedy");

        List<CategoryEntity> entities = Arrays.asList(entity1, entity2, entity3);

        when(entityManager.createQuery(anyString(), eq(CategoryEntity.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(entities);

        // Act
        List<Category> result = categoryPersistenceAdapter.findAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Adventure", result.get(0).getName());
        assertEquals("Biography", result.get(1).getName());
        assertEquals("Comedy", result.get(2).getName());
        verify(entityManager, times(1)).createQuery(
            "SELECT c FROM CategoryEntity c ORDER BY c.name", 
            CategoryEntity.class
        );
    }

    @Test
    public void findAllCategories_ShouldMapEntityToDomainCorrectly() {
        // Arrange
        CategoryEntity entity = new CategoryEntity();
        entity.setId(42L);
        entity.setName("Test Category");

        List<CategoryEntity> entities = Collections.singletonList(entity);

        when(entityManager.createQuery(anyString(), eq(CategoryEntity.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(entities);

        // Act
        List<Category> result = categoryPersistenceAdapter.findAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Category category = result.get(0);
        assertEquals(42L, category.getId());
        assertEquals("Test Category", category.getName());
    }
}
