package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.adapters.out.persistence.entity.LanguageEntity;
import com.amool.hexagonal.domain.model.Language;
import java.util.List;
import java.util.Optional;

public interface LoadLanguagePort {
    List<Language> loadAllLanguages();

    Optional<Language> loadLanguageById(Long languageId);

    List<Language> getLanguagesByCodes(List<String> codes);
}
