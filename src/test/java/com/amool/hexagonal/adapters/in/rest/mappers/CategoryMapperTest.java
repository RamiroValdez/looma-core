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
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        CategoryDto result = CategoryMapper.toDto(category);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Fiction", result.getName());
    }

    @Test
    public void toDto_ShouldReturnNull_WhenCategoryIsNull() {
        CategoryDto result = CategoryMapper.toDto(null);

        assertNull(result);
    }

    @Test
    public void toDtoList_ShouldMapCategoryListToCategoryDtoList() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science");

        List<Category> categories = Arrays.asList(category1, category2);

        List<CategoryDto> result = CategoryMapper.toDtoList(categories);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Fiction", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Science", result.get(1).getName());
    }

    @Test
    public void toDtoList_ShouldReturnEmptyList_WhenCategoryListIsEmpty() {
        List<Category> categories = Collections.emptyList();

        List<CategoryDto> result = CategoryMapper.toDtoList(categories);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toDtoList_ShouldReturnNull_WhenCategoryListIsNull() {
        List<CategoryDto> result = CategoryMapper.toDtoList(null);

        assertNull(result);
    }

    @Test
    public void toDtoList_ShouldHandleNullElementsInList() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");

        List<Category> categories = Arrays.asList(category1, null);

        List<CategoryDto> result = CategoryMapper.toDtoList(categories);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNull(result.get(1));
    }
}
