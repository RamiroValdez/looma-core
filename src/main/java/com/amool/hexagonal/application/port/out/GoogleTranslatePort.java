package com.amool.hexagonal.application.port.out;

public interface GoogleTranslatePort {

    public String translateText(String text, String targetLanguage);

}
