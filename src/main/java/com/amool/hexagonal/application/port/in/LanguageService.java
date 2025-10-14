package com.amool.hexagonal.application.port.in;

import com.amool.hexagonal.domain.model.Language;
import java.util.List;

public interface LanguageService {
    List<Language> getAllLanguages();
}
