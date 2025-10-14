package com.amool.hexagonal.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amool.hexagonal.adapters.in.rest.dtos.CategoryDto;
import com.amool.hexagonal.adapters.in.rest.mappers.CategoryMapper;
import com.amool.hexagonal.application.port.in.CategoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/obtain-all")
    public ResponseEntity<List<CategoryDto>> obtainAllCategories() {
        return categoryService.obtainAllCategories()
                .map(CategoryMapper::toDtoList)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
