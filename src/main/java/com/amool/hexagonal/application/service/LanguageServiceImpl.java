package com.amool.hexagonal.application.service;

import com.amool.hexagonal.application.port.in.LanguageService;
import com.amool.hexagonal.application.port.out.LoadLanguagePort;
import com.amool.hexagonal.domain.model.Language;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LanguageServiceImpl implements LanguageService {

    private final LoadLanguagePort loadLanguagePort;

    public LanguageServiceImpl(LoadLanguagePort loadLanguagePort) {
        this.loadLanguagePort = loadLanguagePort;
    }

    @Override
    public List<Language> getAllLanguages() {
        return loadLanguagePort.loadAllLanguages();
    }
}
