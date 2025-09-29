package com.amool.hexagonal.application.port.service.mappers;

import com.amool.hexagonal.adapters.in.rest.dtos.WorkResponseDto;
import com.amool.hexagonal.domain.model.Work;

public class WorkMapper {

    public static WorkResponseDto toDto(Work work) {
        if (work == null) {
            return null;
        }
        WorkResponseDto dto = new WorkResponseDto();
        dto.setId(work.getId());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setCompleted(work.isCompleted());
        return dto;
    }

}
