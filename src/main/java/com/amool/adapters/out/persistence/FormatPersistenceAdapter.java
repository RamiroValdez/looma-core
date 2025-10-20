package com.amool.adapters.out.persistence;

import com.amool.adapters.out.persistence.entity.FormatEntity;
import com.amool.adapters.out.persistence.mappers.FormatMapper;
import com.amool.application.port.out.FormatPort;
import com.amool.domain.model.Format;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FormatPersistenceAdapter implements FormatPort {

    private final EntityManager entityManager;

    public  FormatPersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Format> getById(Long formatId) {
        FormatEntity formatEntity = entityManager.find(FormatEntity.class, formatId);
        return Optional.ofNullable(FormatMapper.toDomain(formatEntity));
    }

    @Override
    public List<Format> getAll() {
        List<FormatEntity> entities = entityManager.createQuery("SELECT f FROM FormatEntity f", FormatEntity.class).getResultList();
        return entities.stream()
                .map(FormatMapper::toDomain)
                .toList();
    }
}
