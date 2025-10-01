package com.amool.hexagonal.application.port.in;

public interface CreateLanguageVersionUseCase {

    public String execute(String sourceLanguage, String targetLanguage, String originalText);

}
