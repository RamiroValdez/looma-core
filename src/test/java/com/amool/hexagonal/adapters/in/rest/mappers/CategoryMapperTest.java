package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.CategoryDto;
import com.amool.hexagonal.domain.model.Category;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryMapperTest {

    @Test
    public void toDto_ShouldMapCategoryToCategoryDto() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        // Act
        CategoryDto result = CategoryMapper.toDto(category);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Fiction", result.getName());
    }

    @Test
    public void toDto_ShouldReturnNull_WhenCategoryIsNull() {
        // Act
        CategoryDto result = CategoryMapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toDtoList_ShouldMapCategoryListToCategoryDtoList() {
        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science");

        List<Category> categories = Arrays.asList(category1, category2);

        // Act
        List<CategoryDto> result = CategoryMapper.toDtoList(categories);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Fiction", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Science", result.get(1).getName());
    }

    @Test
    public void toDtoList_ShouldReturnEmptyList_WhenCategoryListIsEmpty() {
        // Arrange
        List<Category> categories = Collections.emptyList();

        // Act
        List<CategoryDto> result = CategoryMapper.toDtoList(categories);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toDtoList_ShouldReturnNull_WhenCategoryListIsNull() {
        // Act
        List<CategoryDto> result = CategoryMapper.toDtoList(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toDtoList_ShouldHandleNullElementsInList() {
        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");

        List<Category> categories = Arrays.asList(category1, null);

        // Act
        List<CategoryDto> result = CategoryMapper.toDtoList(categories);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNull(result.get(1));
    }
}
