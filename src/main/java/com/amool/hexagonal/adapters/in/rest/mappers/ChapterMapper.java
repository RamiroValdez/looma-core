package com.amool.hexagonal.adapters.in.rest.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.ChapterDto;
import com.amool.hexagonal.domain.model.Chapter;

import java.util.List;
import java.util.stream.Collectors;

public class ChapterMapper {

    public static ChapterDto toDto(Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        ChapterDto dto = new ChapterDto();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setPrice(chapter.getPrice());
        dto.setLikes(chapter.getLikes());
        dto.setLastModified(chapter.getLastModified());
        return dto;
    }

    public static List<ChapterDto> toDtoList(List<Chapter> chapters) {
        if (chapters == null) {
            return null;
        }
        return chapters.stream()
                .map(ChapterMapper::toDto)
                .collect(Collectors.toList());
    }
}
