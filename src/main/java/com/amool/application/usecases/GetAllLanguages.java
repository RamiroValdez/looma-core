package com.amool.application.usecases;

import com.amool.application.port.out.LoadLanguagePort;
import com.amool.domain.model.Language;
import java.util.List;

public class GetAllLanguages {

    private final LoadLanguagePort loadLanguagePort;

    public GetAllLanguages(LoadLanguagePort loadLanguagePort) {
        this.loadLanguagePort = loadLanguagePort;
    }

    public List<Language> execute() {
        return loadLanguagePort.loadAllLanguages();
    }
}
