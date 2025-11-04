package com.amool.adapters.in.rest.mappers;

import com.amool.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.adapters.in.rest.dtos.WorkSearchFilterDto;
import com.amool.domain.model.Work;
import com.amool.domain.model.WorkSearchFilter;

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
        dto.setOriginalLanguage(LanguageMapper.toDto(work.getOriginalLanguage()));
        dto.setChapters(work.getChapters().stream()
                .map(ChapterMapper::toDto)
                .collect(Collectors.toList()));
        dto.setCategories(work.getCategories().stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList()));
        dto.setTags(work.getTags().stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toList()));
        if (work.getPublicationDate() != null) {
            dto.setPublicationDate(java.sql.Date.valueOf(work.getPublicationDate()));
        }
        dto.setLikedByUser(work.getLikedByUser());
        dto.setAverageRating(work.getAverageRating());
        return dto;
    }

    public static WorkSearchFilter toDomain(WorkSearchFilterDto dto) {
        if (dto == null) {
            return null;
        }
        WorkSearchFilter domain = new WorkSearchFilter();
        domain.setCategoryIds(dto.categoryIds());
        domain.setFormatIds(dto.formatIds());
        domain.setRangeEpisodes(dto.rangeEpisodes());
        domain.setLastUpdated(dto.lastUpdated());
        domain.setState(dto.state());
        domain.setMinLikes(dto.minLikes());
        domain.setText(dto.text());
        domain.setSortBy(dto.sortBy());

        if (dto.asc() != null) {
            domain.setAsc(dto.asc());
        }
        return domain;
    }

    public static WorkSearchFilterDto toDto(WorkSearchFilter domain) {
        if (domain == null) {
            return null;
        }
        return new WorkSearchFilterDto(
                domain.getCategoryIds(),
                domain.getFormatIds(),
                domain.getRangeEpisodes(),
                domain.getLastUpdated(),
                domain.getState(),
                domain.getMinLikes(),
                domain.getText(),
                domain.getSortBy(),
                domain.getAsc()
        );
    }

}
