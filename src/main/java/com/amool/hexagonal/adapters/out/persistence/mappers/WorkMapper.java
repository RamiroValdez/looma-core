package com.amool.hexagonal.adapters.out.persistence.mappers;

import com.amool.hexagonal.domain.model.Work;

public class WorkMapper {

    public static Work toDomain(WorkEntity entity) {
        if (entity == null) {
            return null;
        }
        Work work = new Work();
        work.setId(entity.getId());
        work.setTitle(entity.getTitle());
        work.setDescription(entity.getDescription());
        work.setCompleted(entity.isCompleted());

        return work;
    }

}
