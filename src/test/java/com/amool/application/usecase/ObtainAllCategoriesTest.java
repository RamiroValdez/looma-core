package com.amool.application.usecase;

import com.amool.application.port.out.CategoryPort;
import com.amool.domain.model.Category;
import com.amool.application.usecases.ObtainAllCategories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ObtainAllCategoriesTest {

    private CategoryPort categoryPort;
    private ObtainAllCategories useCase;

    @BeforeEach
    public void setUp() {
        categoryPort = Mockito.mock(CategoryPort.class);
        useCase = new ObtainAllCategories(categoryPort);
    }

    @Test
    public void when_CategoriesExist_ThenReturnCategoriesList() {
        Category category1 = createCategory(1L, "Fiction");
        Category category2 = createCategory(2L, "Science");
        List<Category> expectedCategories = Arrays.asList(category1, category2);
        
        when(categoryPort.findAllCategories()).thenReturn(expectedCategories);

        Optional<List<Category>> result = useCase.execute();

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals("Fiction", result.get().get(0).getName());
        assertEquals("Science", result.get().get(1).getName());
    }

    @Test
    public void when_NoCategoriesExist_ThenReturnEmpty() {
        when(categoryPort.findAllCategories()).thenReturn(Collections.emptyList());

        Optional<List<Category>> result = useCase.execute();

        assertTrue(result.isEmpty());
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
