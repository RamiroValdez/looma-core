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

    private void givenCategoriesExist(List<Category> categories) {
        when(categoryPort.findAllCategories()).thenReturn(categories);
    }

    private void givenNoCategories() {
        when(categoryPort.findAllCategories()).thenReturn(Collections.emptyList());
    }

    private Optional<List<Category>> whenObtainAllCategories() {
        return useCase.execute();
    }

    private void thenResultHasCategories(Optional<List<Category>> result, int expectedSize, List<String> expectedNames) {
        assertTrue(result.isPresent(), "Se esperaba lista de categorías presente");
        assertEquals(expectedSize, result.get().size(), "Tamaño inesperado de categorías");
        for (int i = 0; i < expectedNames.size(); i++) {
            assertEquals(expectedNames.get(i), result.get().get(i).getName(), "Nombre de categoría inesperado en índice " + i);
        }
    }

    private void thenResultEmpty(Optional<List<Category>> result) {
        assertTrue(result.isEmpty(), "Se esperaba Optional vacío");
    }

    @Test
    public void when_CategoriesExist_ThenReturnCategoriesList() {
        Category category1 = createCategory(1L, "Fiction");
        Category category2 = createCategory(2L, "Science");
        givenCategoriesExist(Arrays.asList(category1, category2));

        Optional<List<Category>> result = whenObtainAllCategories();

        thenResultHasCategories(result, 2, List.of("Fiction", "Science"));
    }

    @Test
    public void when_NoCategoriesExist_ThenReturnEmpty() {
        givenNoCategories();

        Optional<List<Category>> result = whenObtainAllCategories();

        thenResultEmpty(result);
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
