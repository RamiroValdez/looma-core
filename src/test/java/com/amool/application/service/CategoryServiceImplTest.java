package com.amool.application.service;
/*
import com.amool.application.port.out.CategoryPort;
import com.amool.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    @Mock
    private CategoryPort categoryPort;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void obtainAllCategories_ShouldReturnCategories_WhenCategoriesExist() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science");

        List<Category> expectedCategories = Arrays.asList(category1, category2);
        when(categoryPort.findAllCategories()).thenReturn(expectedCategories);

        Optional<List<Category>> result = categoryService.obtainAllCategories();

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals("Fiction", result.get().get(0).getName());
        assertEquals("Science", result.get().get(1).getName());
        verify(categoryPort, times(1)).findAllCategories();
    }

    @Test
    public void obtainAllCategories_ShouldReturnEmpty_WhenNoCategoriesExist() {
        when(categoryPort.findAllCategories()).thenReturn(Collections.emptyList());

        Optional<List<Category>> result = categoryService.obtainAllCategories();

        assertTrue(result.isEmpty());
        verify(categoryPort, times(1)).findAllCategories();
    }

    @Test
    public void obtainAllCategories_ShouldReturnSingleCategory_WhenOnlyOneCategoryExists() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");

        List<Category> expectedCategories = Collections.singletonList(category);
        when(categoryPort.findAllCategories()).thenReturn(expectedCategories);

        Optional<List<Category>> result = categoryService.obtainAllCategories();

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("Fantasy", result.get().get(0).getName());
        verify(categoryPort, times(1)).findAllCategories();
    }
}
*/