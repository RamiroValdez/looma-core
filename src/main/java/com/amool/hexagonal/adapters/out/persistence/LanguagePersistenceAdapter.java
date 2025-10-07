package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.LanguageMapper;
import com.amool.hexagonal.adapters.out.persistence.repository.LanguageRepository;
import com.amool.hexagonal.application.port.out.LoadLanguagePort;
import com.amool.hexagonal.domain.model.Language;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LanguagePersistenceAdapter implements LoadLanguagePort {

    private final LanguageRepository languageRepository;

    public LanguagePersistenceAdapter(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public List<Language> loadAllLanguages() {
        List<LanguageEntity> languageEntities = languageRepository.findAll();
        return languageEntities.stream()
                .map(LanguageMapper::toDomain)
                .collect(Collectors.toList());
    }
}
