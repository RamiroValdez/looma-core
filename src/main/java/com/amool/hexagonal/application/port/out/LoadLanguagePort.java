package com.amool.hexagonal.application.port.out;

import com.amool.hexagonal.domain.model.Language;
import java.util.List;

public interface LoadLanguagePort {
    List<Language> loadAllLanguages();
}
