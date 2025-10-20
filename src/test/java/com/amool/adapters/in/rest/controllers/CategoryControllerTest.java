package com.amool.adapters.in.rest.controllers;

/*
import com.amool.application.port.in.CategoryService;
import com.amool.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void obtainAllCategories_ShouldReturnCategories_WhenCategoriesExist() throws Exception {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science");

        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.obtainAllCategories()).thenReturn(Optional.of(categories));

        mockMvc.perform(get("/api/category/{obtain-all}", "obtain-all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Fiction"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Science"));
    }

    @Test
    public void obtainAllCategories_ShouldReturnNotFound_WhenNoCategoriesExist() throws Exception {
        when(categoryService.obtainAllCategories()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/category/{obtain-all}", "obtain-all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void obtainAllCategories_ShouldReturnEmptyArray_WhenServiceReturnsEmptyList() throws Exception {
        when(categoryService.obtainAllCategories()).thenReturn(Optional.of(Collections.emptyList()));

        mockMvc.perform(get("/api/category/{obtain-all}", "obtain-all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void obtainAllCategories_ShouldReturnSingleCategory_WhenOnlyOneCategoryExists() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");

        List<Category> categories = Collections.singletonList(category);
        when(categoryService.obtainAllCategories()).thenReturn(Optional.of(categories));

        mockMvc.perform(get("/api/category/{obtain-all}", "obtain-all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Fantasy"));
    }
}
*/