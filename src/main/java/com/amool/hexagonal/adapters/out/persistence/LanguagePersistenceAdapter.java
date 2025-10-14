package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.FormatEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.LanguageMapper;
import com.amool.hexagonal.adapters.out.persistence.repository.LanguageRepository;
import com.amool.hexagonal.application.port.out.LoadLanguagePort;
import com.amool.hexagonal.domain.model.Language;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LanguagePersistenceAdapter implements LoadLanguagePort {

    private final EntityManager entityManager;

    public LanguagePersistenceAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Language> loadAllLanguages() {
        List<LanguageEntity> languageEntities = entityManager
                .createQuery("SELECT l FROM LanguageEntity l", LanguageEntity.class)
                .getResultList();
        return languageEntities.stream()
                .map(LanguageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Language> loadLanguageById(Long languageId) {
        LanguageEntity languageEntity = entityManager.find(LanguageEntity.class, languageId);
        return Optional.ofNullable(LanguageMapper.toDomain(languageEntity));
    }

    @Override
    public List<Language> getLanguagesByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }

        List<LanguageEntity> languages = entityManager.createQuery(
                                        "SELECT l FROM LanguageEntity l WHERE l.code IN :codes", LanguageEntity.class)
                                        .setParameter("codes", codes)
                                        .getResultList();


        return languages.stream()
                .map(LanguageMapper::toDomain)
                .collect(Collectors.toList());
    }

}
