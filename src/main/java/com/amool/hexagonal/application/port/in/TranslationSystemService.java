package com.amool.hexagonal.application.port.in;

import java.io.IOException;

public interface TranslationSystemService {

    String CreateLanguageVersion(String sourceLanguage, String targetLanguage, String originalText);

}
