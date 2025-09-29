package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.mappers.WorkMapper;
import com.amool.hexagonal.application.port.out.ObtainWorkByIdPort;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class WorksPersistenceAdapter implements ObtainWorkByIdPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Work execute(Long workId) {

        WorkEntity entity = entityManager.find(WorkEntity.class, workId);

        if (entity == null) {
            return null;
        }

        Work work = WorkMapper.toDomain(entity);

        return work;
    }
}

// Controller -> Interface (Use Case) -> Service (Use Case Implementation) -> Interface (Port) -> Adapter (Persistence) -> Database