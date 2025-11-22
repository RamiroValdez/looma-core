package com.amool.adapters.out.persistence.mappers;

import com.amool.adapters.out.persistence.entity.WorkSavedEntity;
import com.amool.domain.model.WorkSaved;

public class AnalyticsSavedWorkMapper {
    
    public static WorkSaved toDomain(WorkSavedEntity workSavedEntity) {
        return new WorkSaved(
                workSavedEntity.getId(),
                workSavedEntity.getUser().getId(),
                workSavedEntity.getWork().getId(),
                workSavedEntity.getSavedAt()
        );
    }
}
