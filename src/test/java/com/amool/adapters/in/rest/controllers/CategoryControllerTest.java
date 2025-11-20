package com.amool.adapters.in.rest.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amool.adapters.in.rest.dtos.CategoryDto;
import com.amool.application.port.out.CategoryPort;
import com.amool.application.usecases.ObtainAllCategoriesUseCase;
import com.amool.domain.model.Category;

public class CategoryControllerTest {

    private CategoryController categoryController;
    private ObtainAllCategoriesUseCase obtainAllCategoriesUseCase;
    private CategoryPort categoryPort;

    @BeforeEach
    public void setUp() {
        categoryPort = Mockito.mock(CategoryPort.class);
        obtainAllCategoriesUseCase = new ObtainAllCategoriesUseCase(categoryPort);
        categoryController = new CategoryController(obtainAllCategoriesUseCase);
    }

    @Test
    public void when_ObtainAllCategories_ThenReturnCategoryList() {
        givenCategoriesExistInTheSystem();

        ResponseEntity<List<CategoryDto>> response = categoryController.obtainAllCategories();

        thenResponseIsSuccessful(response);
        thenResponseContainsExpectedCategories(response);
    }

    @Test
    public void when_NoCategoriesExist_ThenReturnNotFound() {
        givenNoCategoriesExist();

        ResponseEntity<List<CategoryDto>> response = categoryController.obtainAllCategories();

        thenResponseIsNotFound(response);
    }


    private void givenCategoriesExistInTheSystem() {
        List<Category> categories = createCategoryList();
        when(categoryPort.findAllCategories()).thenReturn(categories);
    }

    private void givenNoCategoriesExist() {
        List<Category> emptyList = new ArrayList<>();
        when(categoryPort.findAllCategories()).thenReturn(emptyList);
    }

    private List<Category> createCategoryList() {
        List<Category> categories = new ArrayList<>();
        categories.add(createCategory(1L, "Ficción"));
        categories.add(createCategory(2L, "No Ficción"));
        categories.add(createCategory(3L, "Ciencia Ficción"));
        return categories;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }


    private void thenResponseIsSuccessful(ResponseEntity<List<CategoryDto>> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private void thenResponseContainsExpectedCategories(ResponseEntity<List<CategoryDto>> response) {
        List<CategoryDto> categories = response.getBody();
        assertEquals(3, categories.size());

        assertCategoryDto(categories.get(0), 1L, "Ficción");
        assertCategoryDto(categories.get(1), 2L, "No Ficción");
        assertCategoryDto(categories.get(2), 3L, "Ciencia Ficción");
    }

    private void assertCategoryDto(CategoryDto categoryDto, Long expectedId, String expectedName) {
        assertEquals(expectedId, categoryDto.getId());
        assertEquals(expectedName, categoryDto.getName());
    }

    private void thenResponseIsNotFound(ResponseEntity<List<CategoryDto>> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
