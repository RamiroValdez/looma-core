package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.FormatEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.FormatMapper;
import com.amool.hexagonal.application.port.out.FormatPort;
import com.amool.hexagonal.domain.model.Format;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

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
}
