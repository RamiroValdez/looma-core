package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.CategoryDto;
import com.amool.adapters.in.rest.dtos.FormatDto;
import com.amool.adapters.in.rest.dtos.WorkListDto;
import com.amool.domain.model.Work;

import java.util.List;
import java.util.stream.Collectors;

public class WorkListMapper {

    public static WorkListDto toDto(Work work) {
        if (work == null) {
            return null;
        }

        WorkListDto dto = new WorkListDto();
        dto.setId(work.getId());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setCover(work.getCover());
        dto.setLikes(work.getLikes());
        
        if (work.getFormat() != null) {
            FormatDto formatDto = new FormatDto();
            formatDto.setId(work.getFormat().getId());
            formatDto.setName(work.getFormat().getName());
            dto.setFormat(formatDto);
        }
        
        if (work.getCategories() != null && !work.getCategories().isEmpty()) {
            List<CategoryDto> categoryDtos = work.getCategories().stream()
                .map(category -> {
                    CategoryDto categoryDto = new CategoryDto();
                    categoryDto.setId(category.getId());
                    categoryDto.setName(category.getName());
                    return categoryDto;
                })
                .collect(Collectors.toList());
            dto.setCategories(categoryDtos);
        }

        return dto;
    }
}