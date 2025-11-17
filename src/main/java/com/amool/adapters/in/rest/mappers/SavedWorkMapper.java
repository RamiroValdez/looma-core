package com.amool.adapters.in.rest.mappers;

import com.amool.domain.model.WorkSaved;
import com.amool.adapters.in.rest.dtos.AnalyticsSavedWorkDto;

public class SavedWorkMapper {

    public static WorkSaved toDomain(AnalyticsSavedWorkDto workSavedEntity) {
        return new WorkSaved(
                workSavedEntity.savedId(),
                workSavedEntity.userId(),
                workSavedEntity.workId(),
                workSavedEntity.savedAt()
        );
    }

    public static AnalyticsSavedWorkDto toDto(WorkSaved workSaved) {
        return new AnalyticsSavedWorkDto(
                workSaved.getId(),
                workSaved.getUserId(),
                workSaved.getWorkId(),
                workSaved.getSavedAt()
        );
    }
}
