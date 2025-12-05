package com.amool.adapters.in.rest.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amool.application.usecases.ObtainAllCategories;
import com.amool.adapters.in.rest.dtos.CategoryDto;
import com.amool.adapters.in.rest.mappers.CategoryMapper;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final ObtainAllCategories obtainAllCategories;

    public CategoryController(ObtainAllCategories obtainAllCategories) {
        this.obtainAllCategories = obtainAllCategories;
    }

    @GetMapping("/obtain-all")
    public ResponseEntity<List<CategoryDto>> obtainAllCategories() {
        return this.obtainAllCategories.execute()
                .map(CategoryMapper::toDtoList)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
