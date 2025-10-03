package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.domain.model.Work;
import java.util.stream.Collectors;

public class WorkMapper {

    public static WorkResponseDto toDto(Work work) {
        if (work == null) {
            return null;
        }
        WorkResponseDto dto = new WorkResponseDto();
        dto.setId(work.getId());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setCover(work.getCover());
        dto.setBanner(work.getBanner());
        dto.setState(work.getState());
        dto.setPrice(work.getPrice());
        dto.setLikes(work.getLikes());
        dto.setCreator(CreatorMapper.toDto(work.getCreator()));
        dto.setFormat(FormatMapper.toDto(work.getFormat()));
        dto.setChapters(work.getChapters().stream()
                .map(ChapterMapper::toDto)
                .collect(Collectors.toList()));
        dto.setCategories(work.getCategories().stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList()));
        if (work.getPublicationDate() != null) {
            dto.setPublicationDate(java.sql.Date.valueOf(work.getPublicationDate()));
        }
        return dto;
    }

}
