package com.amool.application.port.out;

import com.amool.domain.model.Language;
import java.util.List;
import java.util.Optional;

public interface LoadLanguagePort {
    List<Language> loadAllLanguages();

    Optional<Language> loadLanguageById(Long languageId);

    List<Language> getLanguagesByCodes(List<String> codes);
}
